package education.x.util

/***
  * Using(getConnection(...)){
  *   conn => {
  *   }
  * }
  */
object Using {
  def apply[C <: AutoCloseable, R](resource: => C)(function: C => R): R =
    try {
      function(resource)
    } finally {
      try {
        resource.close()
      } catch {
        case throwable: Throwable => throwable.printStackTrace()
      }
    }

}