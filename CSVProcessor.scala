  import scala.io.Source

  object CSVProcessor {
    def main(args: Array[String]): Unit = {
      // Path to the CSV file
      val filePath = "data/users.csv"

      val lines = Source.fromFile(filePath).getLines().toList

      // Split the header and rows
      val header = lines.head.split(",").toList
      val rows = lines.tail.map(_.split(",").toList)

      // Filter users aged 25 and above
      val filteredRows = rows.filter { row =>
        val age = row(2).toInt
        age >= 25
      }

      // Transform data to extract names and cities
      val transformedData = filteredRows.map { row =>
        (row(1), row(3)) // (name, city)
      }

      // Group users by city
      val groupedByCity = transformedData.groupBy(_._2).map { case (city, users) =>
        city -> users.map(_._1)
      }

      // Print the result
      groupedByCity.foreach { case (city, names) =>
        println(s"$city: ${names.mkString(", ")}")
      }

    }
  }


