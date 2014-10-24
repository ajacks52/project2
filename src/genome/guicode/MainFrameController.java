package genome.guicode;

import genome.types.Genome;
import genome.types.Tribe;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.*;
import java.util.Timer;
import java.util.TimerTask;

/*******************************************************************************************************
 * MainFrameController
 * 
 * @author Adam Mitchell
 * @author Jordan Medlock
 * 
 * Where are program will start and the brains of the program
 *******************************************************************************************************/
public class MainFrameController
{
  private static MainFrame frame;
  public static BufferedImage bi = null;

  int minutes;
  int seconds;
  long timeNow;
  long startTime;
  long deltaTime = 0;
  long stoppedTime = 0;
  boolean append = false;
  static Genome displayedGenome;
  static Tribe displayedTribe;
  static int thribesAmount = 0;
  public static int numberOfTribes = 1;
  volatile static int totalgenomes = 0;
  static int[] statsArray = new int[10];
  static String statsfileName = "statsFile";
  public volatile int totalgenerations = 0;
  public volatile static int generationspersec = 0;
  public volatile static int totalmutations = 0;
  public volatile static int totalcrossovers = 0;
  public static ArrayList<Tribe> threads = new ArrayList<Tribe>();
  volatile static boolean paused = false; // Run unless told to pause

  /*******************************************************************************************************
   * Constructor for the main frame controller Gets everything started calls startGA_HC() setTimers()
   ******************************************************************************************************/
  public MainFrameController()
  {
    frame = new MainFrame();
    frame.start();
    startTime = System.currentTimeMillis();
    new GUIActionListeners().setListeners(frame);
    startGA_HC();
    setTimers();
  }

  /*******************************************************************************************************
   * The timers the control some of the flow of the program timer statsFileTimer
   ******************************************************************************************************/
  private void setTimers()
  {
    Timer timer = new Timer();
    timer.scheduleAtFixedRate(new TimerTask() {
      @Override
      public void run()
      {
        timeNow = System.currentTimeMillis();
        if (paused)
        {
          startTime = System.currentTimeMillis() - deltaTime;
        }
        else if (!paused)
        {
          deltaTime = (timeNow - startTime);
          int minutes = (int) ((deltaTime) / 60000);
          int seconds = (int) ((deltaTime) * 0.001);
          seconds %= 60;
          frame.buttonPanel.setTime(minutes, seconds);
        }
      }
    }, 0, 500L);

    timer.scheduleAtFixedRate(new TimerTask() {
      @Override
      public void run()
      {
        if (!paused)
        {
          displayGenome();
        }
      }
    }, 0, 1000L);

    timer.scheduleAtFixedRate(new TimerTask() {
      @Override
      public void run()
      {
        if (threads.size() > 1)
        {
          Tribe.pause();
          for (int i = 0; i < threads.size() - 1; i += 2)
          {

            Tribe t1 = threads.get(i);
            Tribe t2 = threads.get(i + 1);
            while (!t1.fullyPaused || !t1.fullyPaused);
            int div2 = Tribe.TRIBE_SIZE / 2;
            for (int j = 0; j < div2; j++)
            {
              t1.genomes[j].mateWith(t2.genomes[j], t1.genomes[j + div2], t2.genomes[j + div2]);
            }
          }
        }
      }
    }, 0, 5000);

    Timer statsFileTimer = new Timer();
    statsFileTimer.scheduleAtFixedRate(new TimerTask() {
      @Override
      public void run()
      {

        synchronized (threads)
        {
          totalgenerations = totalmutations + totalcrossovers;
          frame.buttonPanel.updateGUIStats(totalgenerations, totalmutations, totalcrossovers, totalgenomes,
              generationspersec);
          statsArray[0] = minutes;
          statsArray[1] = seconds;
          statsArray[2] = totalgenerations;
          statsArray[3] = totalmutations;
          statsArray[4] = totalcrossovers;
          statsArray[5] = 0; // numberOfTribes;
          statsArray[6] = 0; // totalgenomes;
          statsArray[7] = 0; // bestfit;
          statsArray[8] = 0; // averagefitness;
          statsArray[9] = generationspersec;
          generationspersec = 0;
        }

        try
        {
          new PrintStatsFile(statsfileName).writeToFile(append, statsArray);
          append = true;
        }
        catch (IOException e)
        {
          e.printStackTrace();
        }
      }
    }, 0, 60000L);
  }

  /*******************************************************************************************************
   * startGA_HC() Start the genetic algorithm and the hill climbing by calling birth tribe
   ******************************************************************************************************/
  private void startGA_HC()
  {
    frame.buttonPanel.disableButtons();
    frame.disableMenu();
    frame.picturePanel.setPicture("Leonardo_da_Vinci-Mona-Lisa-460x363.png");
    bi = frame.picturePanel.getCurrentPicture();
    LoadPictures.currentPicture(frame.picturePanel.getCurrentPicture());
    birthTribe();
    frame.buttonPanel.setFitnessGenome(0, 0);
  }

  /*******************************************************************************************************
   * displayGenome() Gets called every .5 second and shows the currently selected genome in the triangle panel
   * 
   * @param g
   *******************************************************************************************************/
  public void displayGenome()
  {
    Genome bestG = null;
    synchronized (frame)
    {
      double bestfit = 10000000000L; // really big number

      int bestIndex = 0;
      if (threads.get(0).genomes != null)
      {
        for (int i = 0; i < threads.size(); i++)
        {
          Genome genome = threads.get(i).genomes[0]; // assuming index 0 is the most fit
          double current = genome.getFitness();
          if (current < bestfit)
          {
            bestfit = current;
            bestG = genome;
          }
        }
        frame.buttonPanel.setFitnessTotal(bestfit, bestIndex);
      }
    }
    if (displayedGenome != null)
    {
      frame.trianglePanel.displayGenome(displayedGenome);
      frame.buttonPanel.setFitnessGenome(displayedGenome.getFitness(),
          Integer.valueOf(displayedTribe.getName().substring(6).trim()));
    }
    else
    {
      frame.trianglePanel.displayGenome(bestG);
    }
  }

  /*******************************************************************************************************
   * birthTribe() Births a new tribe adds it the tribe combo box
   *******************************************************************************************************/
  static void birthTribe()
  {
    BufferedImage bImage = frame.picturePanel.getCurrentPicture();
    System.out.println("in tribe's picture is " + bImage.getHeight());
    Tribe tribe = new Tribe("Tribe 1", bImage);
    tribe.start();
    tribe.setName("Tribe " + (new Integer(++thribesAmount).toString()));
    totalgenomes += tribe.genomes.length;
    threads.add(tribe);
    frame.buttonPanel.setComboxTribe(tribe);
  }

  /*******************************************************************************************************
   * killTribe() Kills a tribe removes it from the tribe combo box
   *******************************************************************************************************/
  static void killTribe()
  {
    totalgenomes -= threads.get(threads.size() - 1).genomes.length;
    threads.get(threads.size() - 1).interrupt();

    frame.buttonPanel.deleteComboxTribe(thribesAmount - 1);
    threads.remove(threads.size() - 1);
    thribesAmount--;
  }

  /*******************************************************************************************************
   * restart(BufferedImage bImage, ArrayList<Integer> clist) Sets up a new picture in the triangle panel restarts all
   * the treads/tribes clears out the tribe and genome combo boxes
   * 
   * @param bImage
   * @param clist
   *******************************************************************************************************/
  static void restart(BufferedImage bImage, ArrayList<Integer> clist)
  {
    System.out.println("Restarting GA / HC with new picture");

    for (int i = 0; i < threads.size(); i++)
    {
      killTribe();
    }
    System.out.println("Number of tribes to be made " + numberOfTribes);
    for (int i = 0; i < numberOfTribes; i++)
    {
      birthTribe();
    }
  }

  /*******************************************************************************************************
   * getCurrentPict() ges the current picture
   * 
   * @return BufferedImage
   *******************************************************************************************************/
  public static BufferedImage getCurrentPict()
  {
    return bi;
  }

  /*******************************************************************************************************
   * Main starts the whole program..
   *******************************************************************************************************/
  public static void main(String[] args)
  {
    new MainFrameController();
  }
}
