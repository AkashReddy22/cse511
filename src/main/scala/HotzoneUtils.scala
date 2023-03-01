package cse511

object HotzoneUtils {

  def ST_Contains(queryRectangle: String, pointString: String ): Boolean = {

    // YOU NEED TO CHANGE THIS PART        
    
    val rect = queryRectangle.spit(",")
    val target = pointString.split(",")

    val pt_x: Double = target(0).trim.toDouble
    val pt_y : Double = target(1).trim.toDouble

    val rect_x1: Double = Math.min(rect(0).trim.toDouble, rect(2).trim.toDouble)

    val rect_y1: Double =  Math.min(rect(1).trim.toDouble, rect(3).trim.toDouble)

    val rect_x2: Double =   Math.max(rect(0).trim.toDouble, rect(2).trim.toDouble)

    val rect_y2: Double =   Math.max(rect(1).trim.toDouble, rect(3).trim.toDouble)

    if ((pt_x >= rect_x1) && (pt_x<= rect_x2) && (pt_y>=rect_y1) && (pt_y<=rect_y2)){
      return true
    }
    return false

  }
}
