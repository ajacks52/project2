package genome.guicode;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class PrintStatsFile
{
  String address = "data/statsFile.txt";
  File outfile = new File(address);

  public PrintStatsFile(String address)
  {
    if (address.contains(".txt"))
    {
      this.address = "data/" + address;
    }
    else
    {
      this.address = "data/" + address + ".txt";
    }
  }

  public void writeToFile(boolean newfile,Integer[] stats) throws IOException
  {
    FileWriter writer = new FileWriter(this.address, newfile);

    writer.write("----------------------Statistics Print 1------------------------"
        + System.getProperty("line.separator"));
    writer.write("Total Time Running" + System.getProperty("line.separator"));
    writer.write("m:s "+stats[0]+":"+stats[1] + System.getProperty("line.separator"));
    //writer.write( System.getProperty("line.separator"));
    writer.write("Total Generations" + System.getProperty("line.separator") );
    writer.write(stats[2] + System.getProperty("line.separator"));
    writer.write("Hill Climbing Generations" + System.getProperty("line.separator"));
    writer.write(stats[3] + System.getProperty("line.separator"));
    writer.write("Genetic Algorithm Generations" + System.getProperty("line.separator"));
    writer.write(stats[4] + System.getProperty("line.separator"));
    writer.write("Tribe/Population Diversity" + System.getProperty("line.separator"));
    writer.write("# of tribes "+stats[5] + System.getProperty("line.separator"));
    writer.write("# of genomes "+stats[6] + System.getProperty("line.separator"));
    writer.write("Overall Best fitness" + System.getProperty("line.separator"));
    writer.write(stats[7] + System.getProperty("line.separator"));
    writer.write("average fitness of tribes " +stats[8]+ System.getProperty("line.separator"));
    writer.write("----------------------------------------------------------------"
        + System.getProperty("line.separator"));
    writer.flush();
    writer.close();
    System.out.println("Wrote file");
  }

  /**
   * main for testing
   * @param args
   */
  public static void main(String[] args)
  {
    
  }

}
