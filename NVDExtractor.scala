import java.io.{File, PrintWriter}
import scala.io.Source
import ujson._

object NVDExtractor {
  def main(args: Array[String]): Unit = {
    // Path to the data folder
    val dataFolderPath = "data/all-cve"
    // Path to the destination JSON file
    val destinationFilePath = "extracted_nvd.json"

    // List to hold the extracted CVE information
    var extractedCVEs: List[ujson.Value] = List()

    // Read and process each JSON file in the data folder
    val dataFolder = new File(dataFolderPath)
    if (dataFolder.exists() && dataFolder.isDirectory) {
      dataFolder.listFiles().filter(_.getName.endsWith(".json")).foreach { file =>
        val jsonString = Source.fromFile(file).mkString
        val json = ujson.read(jsonString)

        // Extract the required information
        val cveItems = json("CVE_Items").arr.map { cveItem =>
          val id = cveItem("cve")("CVE_data_meta")("ID").str
          val description = cveItem("cve")("description")("description_data")(0)("value").str
          val baseScore = cveItem("impact")("baseMetricV3")("cvssV3")("baseScore").num
          val baseSeverity = cveItem("impact")("baseMetricV3")("cvssV3")("baseSeverity").str
          val exploitabilityScore = cveItem("impact")("baseMetricV3")("exploitabilityScore").num
          val impactScore = cveItem("impact")("baseMetricV3")("impactScore").num

          ujson.Obj(
            "ID" -> id,
            "Description" -> description,
            "baseScore" -> baseScore,
            "baseSeverity" -> baseSeverity,
            "exploitabilityScore" -> exploitabilityScore,
            "impactScore" -> impactScore
          )
        }

        // Add the extracted CVEs to the list
        extractedCVEs = extractedCVEs ++ cveItems
      }
    }

    // Write the extracted information to the destination JSON file
    val destinationFile = new PrintWriter(new File(destinationFilePath))
    destinationFile.write(ujson.Arr(extractedCVEs: _*).toString())
    destinationFile.close()

    println(s"Extracted CVE information has been written to $destinationFilePath")
  }
}
