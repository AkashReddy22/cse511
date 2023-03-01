package cse511

import org.apache.log4j.{Level, Logger}
import org.apache.spark.sql.{DataFrame, SparkSession}
import org.apache.spark.sql.functions.udf
import org.apache.spark.sql.functions._

object HotcellAnalysis {
  Logger.getLogger("org.spark_project").setLevel(Level.WARN)
  Logger.getLogger("org.apache").setLevel(Level.WARN)
  Logger.getLogger("akka").setLevel(Level.WARN)
  Logger.getLogger("com").setLevel(Level.WARN)

    def runHotcellAnalysis(spark: SparkSession, pointPath: String): DataFrame =
    {
    // Load the original data from a data source
    var pickupInfo = spark.read.format("com.databricks.spark.csv").option("delimiter",";").option("header","false").load(pointPath);
    pickupInfo.createOrReplaceTempView("nyctaxitrips")
    pickupInfo.show()

    // Assign cell coordinates based on pickup points
    spark.udf.register("CalculateX",(pickupPoint: String)=>((
      HotcellUtils.CalculateCoordinate(pickupPoint, 0)
      )))
    spark.udf.register("CalculateY",(pickupPoint: String)=>((
      HotcellUtils.CalculateCoordinate(pickupPoint, 1)
      )))
    spark.udf.register("CalculateZ",(pickupTime: String)=>((
      HotcellUtils.CalculateCoordinate(pickupTime, 2)
      )))
    pickupInfo = spark.sql("select CalculateX(nyctaxitrips._c5),CalculateY(nyctaxitrips._c5), CalculateZ(nyctaxitrips._c1) from nyctaxitrips")
    var newCoordinateName = Seq("x", "y", "z")
    pickupInfo = pickupInfo.toDF(newCoordinateName:_*)
    pickupInfo.show()

    // Define the min and max of x, y, z
    val minX = -74.50/HotcellUtils.coordinateStep
    val maxX = -73.70/HotcellUtils.coordinateStep
    val minY = 40.50/HotcellUtils.coordinateStep
    val maxY = 40.90/HotcellUtils.coordinateStep
    val minZ = 1
    val maxZ = 31
    val numCells = (maxX - minX + 1)*(maxY - minY + 1)*(maxZ - minZ + 1)

    // YOU NEED TO CHANGE THIS 
    
     pickupInfo.createOrReplaceTempView("pickupInfo") 
    val points_req = spark.sql("select x,y,z,count(*) as val_count from pickupInfo where x>=" + minX + " and x<=" + maxX + " and y>="+minY +" and y<="+maxY+" and z>="+minZ+" and z<=" +maxZ +" group by x,y,z").persist()
    
     points_req.createOrReplaceTempView("points_req")  
    
     val pt = spark.sql("select sum(val_count) as sumVal, sum(val_count*val_count) as sqr_sum from points_req").persist()
    val val_sum = pt.first().getLong(0).toDouble
    val sqr_sum = pt.first().getLong(1).toDouble

    val mean = (val_sum/numCells)
    val standard_deviation = Math.sqrt((sqr_sum/numCells)-(mean*mean))

    val neighbor_condn = spark.sql("select gp1.x as x , gp1.y as y, gp1.z as z, count(*) as numOfNb, sum(gp2.countVal) as sigma from points_req as gp1 inner join points_req as gp2 on ((abs(gp1.x-gp2.x) <= 1 and  abs(gp1.y-gp2.y) <= 1 and abs(gp1.z-gp2.z) <= 1)) group by gp1.x, gp1.y, gp1.z").persist()
    neighbor_condn.createOrReplaceTempView("neighbor_condn")

    spark.udf.register("Zscorecalculation",(mean: Double, stddev:Double, numOfNb: Int, sigma: Int, numCells:Int)=>((
    HotcellUtils.Zscorecalculation(mean, stddev, numOfNb, sigma, numCells)
    )))  

    val Zscoredata = spark.sql("select x,y,z,Zscorecalculation("+ mean + ","+ standard_deviation +",numOfNb,sigma," + numCells+") as zscore from neighbor_condn")

    Zscoredata.createOrReplaceTempView("Zscoredata")

    val return_val = spark.sql("select x,y,z from Zscoredata order by zscore desc")
    return return_val
    

    }
}
