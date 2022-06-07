// Databricks notebook source
import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}
import scala.collection.immutable.ListMap


object Taxonomy_Test3 {
  def main(args: Array[String]): Unit = {
  val sc: SparkContext= SparkContext.getOrCreate()
//创建RDD
//默认从文件中读取的数据都是字符串类型
    //val inpath="dbfs:/mnt/share.jgi-ga.org/chen-Tax/CAMI2_Human/CAMI2_human_allreads_tax_species_df.csv"
    //val outpath="dbfs:/mnt/share.jgi-ga.org/chen-Tax/CAMI2_Human/CAMI2_human_allreads_tax_species_V3_0.5"
    val threshold=0.5
  var fileRdd = sc.textFile(inpath)

    val first = fileRdd.first()
    println(first)
    val broadfirst = sc.broadcast(first)
    val fileRdd2=fileRdd.filter((row: String) => row!=first)
    val mapRdd = fileRdd2.map((line: String) => line.trim.split(","))
    val groupRdd = mapRdd
      .groupBy((arr: Array[String]) => arr(0))
      .map { collections: (String, Iterable[Array[String]]) =>
        val array = collections._2
        var clark_maxval = ""
        var clark_NonZeroTotalNum = 0d
        var clark_totalNum=0d
        var clark_map: Map[String, Int] = Map()

        for (elem <- array) {
          val clark_species = elem(8)
          if(clark_species!="0") {
            clark_NonZeroTotalNum += 1
          }
          clark_totalNum +=1
          clark_map += (clark_species -> (clark_map.getOrElse(clark_species, 0) + 1));
        }
        //delete the unclassified reads
        clark_map-=("0")

        val clark_sortedmap = ListMap(clark_map.toSeq.sortWith(_._2 > _._2): _*)

        if(clark_NonZeroTotalNum/clark_totalNum < 0.2  || clark_sortedmap.isEmpty){
          clark_maxval="0"
        }else if (clark_sortedmap.size == 1) {
          clark_maxval = clark_sortedmap.toSeq.head._1
        } else {
          val clark_seq = clark_sortedmap.take(2).toSeq
          val clark_max1 = clark_seq.head
          if(clark_max1._1 != "-1" && clark_max1._2/clark_NonZeroTotalNum>=threshold){
            clark_maxval=clark_max1._1
          }else{
            clark_maxval="0"
          }
        }
        for (elem <- array) {
          elem(3) = clark_maxval;
        }
        
        
        var maxval = ""
        var NonZeroTotalNum = 0d
        var totalNum=0d
        var map: Map[String, Int] = Map()

        for (elem <- array) {
          val kra_species = elem(9)
          if(kra_species!="0") {
            NonZeroTotalNum += 1
          }
          totalNum +=1
          map += (kra_species -> (map.getOrElse(kra_species, 0) + 1));
        }
        //delete the unclassified reads
        map-=("0")

        val sortedmap = ListMap(map.toSeq.sortWith(_._2 > _._2): _*)

        if(NonZeroTotalNum/totalNum < 0.2  || sortedmap.isEmpty){
          maxval="0"
        }else if (sortedmap.size == 1) {
          maxval = sortedmap.toSeq.head._1
        } else {
          val seq = sortedmap.take(2).toSeq
          val max1 = seq.head
          if(max1._1 != "-1" && max1._2/NonZeroTotalNum>=threshold){
            maxval=max1._1
          }else{
            maxval="0"
          }
        }
        
        for (elem <- array) {
          elem(2) = maxval;
        } 
         
        array
      }.flatMap((x: Iterable[Array[String]]) => x)

    val resultRdd = groupRdd.map((arr: Array[String]) =>arr.mkString(","))
    resultRdd.take(5).foreach(println(_))
    resultRdd.repartition(1).saveAsTextFile(outpath)
    println("======================finished====================")

}
  
}
 

