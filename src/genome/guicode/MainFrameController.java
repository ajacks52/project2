package genome.guicode;

import genome.Constants;
import genome.logic.PictureResize;
import genome.types.Genome;
import genome.types.Triangle;
import genome.types.Tribe;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.filechooser.*;

/***********************************************************************************
 * Where are program will start and the brains of the program
 ***********************************************************************************/
public class MainFrameController
{
  private MainFrame frame;
  private Tribe tribe;
  private static BufferedImage bi = null;
  private static BufferedImage smallBi = null;

  private int numTribe = 0;
  private Timer totalRunningTime = new Timer();
  volatile int totalgenerations = 0;
  volatile int generationspersec = 0;
  public volatile static int totalmutations = 0;
  public volatile static int totalcrossovers = 0;
  public int totaltribes;
  volatile int totalgenomes = 0;
  int sec = 0;
  int min = 0;
  Integer[] statsArray = new Integer[9];


  int width;
  int height;

  private volatile boolean paused = false; // Run unless told to pause
  private volatile int generations = 0;

  public MainFrameController()
  {
    // SwingUtilities.invokeLater(new MainFrame());
    frame = new MainFrame();
    frame.start(); // needs to be changed so it'll sleep

    /*
     * All the action listeners are below pauseButton, picturePicker, genomePicker, tribeSelector, triangleSelector,
     * nextButton
     */

    /*
     * pauseButton
     */
    frame.buttonPanel.addStartPauseActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e)
      {
        if (frame.buttonPanel.setPause())
        {

          frame.buttonPanel.enableButtons();
          frame.enableMenu();
        }
        else
        {
          frame.buttonPanel.disableButtons();
          frame.disableMenu();
        }
        paused = frame.buttonPanel.getPauseState();
      }
    });

    /*
     * picturePicker
     */
    frame.buttonPanel.addPicturePickerActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e)
      {
        @SuppressWarnings("unchecked")
        JComboBox<String> cb = (JComboBox<String>) e.getSource();
        String pictName = (String) cb.getSelectedItem();
        frame.picturePanel.setPicture(pictName);
        bi = frame.picturePanel.getCurrentPicture();
        smallBi = PictureResize.resize(bi, Constants.RESIZED_PICTURE_SIZE, Constants.RESIZED_PICTURE_SIZE);

        ArrayList<Integer> colorList = frame.picturePanel.pictureColorValues(bi);
        frame.picturePanel.setColorList(colorList);
        LoadPictures.currentPicture(bi);

        restart(bi, colorList);
        System.out.println("Selected picture: " + pictName);
      }
    });

    /*
     * genomePicker
     */
    frame.buttonPanel.addGenomePickerActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e)
      {
        System.out.println("Unimplemented method for genomePicker");
      }
    });

    /*
     * tribeSelector
     */
    frame.buttonPanel.addTribeSelectorActionListener(new ChangeListener() {
      @Override
      public void stateChanged(ChangeEvent e)
      {
        // System.out.println("tribeSelector");
        int value = (int) ((JSpinner) e.getSource()).getValue();
        frame.buttonPanel.setTribeNumber(value);

        // frame.trianglePanel.displayTriangles( add stuff to display the current tribes triangels);
      }
    });

    /*
     * triangleSelector
     */
    frame.buttonPanel.addTriangleSelectorChangeListener(new ChangeListener() {
      @Override
      public void stateChanged(ChangeEvent e)
      {
        // System.out.println("triangleSelector");
        int value = (int) ((JSlider) e.getSource()).getValue();
        frame.buttonPanel.setTriangleNumber(value);
        frame.trianglePanel.setTriangleCount(value);
      }
    });

    /*
     * nextButton
     */
    frame.buttonPanel.addNextButtonActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e)
      {
        Genome g = Genome.randomGenome(frame.picturePanel.getCurrentPicture().getWidth(), frame.picturePanel
            .getCurrentPicture().getHeight());
        frame.trianglePanel.displayGenome(g);
       // frame.buttonPanel.setFitness(g.getFitness(frame.picturePanel.getCurrentPicture(), 5));
      }
    });

    /*
     * write genome
     */
    frame.addwriteGenomeActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e)
      {
        // TODO needs to be the current genome
        new WriteXMLFile().generate(new Genome(Triangle.randomGenome(200, 200, 200,
            frame.picturePanel.pictureColorValues(frame.picturePanel.getCurrentPicture())), 200, 200));
      }
    });

    /*
     * read genome
     */
    frame.addreadGenomeActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e)
      {
        JFileChooser chooser = new JFileChooser();
        FileFilter filter = new FileNameExtensionFilter("XML files only", "xml");
        chooser.setFileFilter(filter);
        File f = null;
        try
        {
          f = new File(new File(".").getCanonicalPath());
        }
        catch (IOException e1)
        {
          Genome g = Genome.randomGenome(frame.picturePanel.getCurrentPicture().getWidth(), frame.picturePanel
              .getCurrentPicture().getHeight());
          frame.trianglePanel.displayGenome(g);
          //frame.buttonPanel.setFitness(g.getFitness(frame.picturePanel.getCurrentPicture(), 5));

        }
        chooser.setCurrentDirectory(f);
        chooser.showOpenDialog(null);
        File curFile = chooser.getSelectedFile();
        if (!(curFile == null))
        {
          System.out.println(curFile.toString());
          ArrayList<Triangle> xmlArrayListTriangle = new XMLParser().parser(curFile);
          if (xmlArrayListTriangle == null)
          {
            JOptionPane.showMessageDialog(null,
                "Sorry the file you selected was not in the correct xml format we expected"
                    + "\n\n click ok to continue");
          }
          else
          {
            // TODO need make a new genome with the arraylist xmlArrayListTriangle and add it to a tribe..
          }
        }
      }
    });

    tribe = new Tribe("Tribe 1", frame.picturePanel.getCurrentPicture().getWidth(), frame.picturePanel
        .getCurrentPicture().getHeight(), frame.picturePanel.getCurrentPicture(), new ArrayList<Integer>());
    tribe.start();

    int width = frame.picturePanel.getCurrentPicture().getWidth();
    int height = frame.picturePanel.getCurrentPicture().getHeight();
    ArrayList<Integer> colorList = frame.picturePanel.pictureColorValues(LoadPictures.bImage1);

    Genome g = Genome.randomGenome(width, height);

    frame.trianglePanel.displayGenome(g);

//    long fitness = g.getFitness(frame.picturePanel.getCurrentPicture(), 5);
//    frame.buttonPanel.setFitness(fitness);

    /**
     * show table
     */
    frame.addshowTableActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e)
      {
        // TODO need to pass in the current genome not just a random one
        new TableStats().showTableData(new Genome(Triangle.randomGenome(200, 200, 200,
            frame.picturePanel.pictureColorValues(frame.picturePanel.getCurrentPicture())), 200, 200));
      }
    });

    /*
     * Append Stats
     */
    frame.addAppendStatsActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e)
      {
        // TODO need to pass in the current genome not just a random one
        JOptionPane.showMessageDialog(null, "Message", "File saved", JOptionPane.INFORMATION_MESSAGE);
        try
        {
          new PrintStatsFile("file2").writeToFile(true,null);
        }
        catch (IOException e1)
        {
          e1.printStackTrace();
        }
      }
    });

    startGA_HC();

  }

  /***************************************************************************************************
   * 
   **************************************************************************************************/
  private void startGA_HC()
  {

    frame.buttonPanel.disableButtons();
    frame.disableMenu();
    frame.picturePanel.setPicture("triangles.png");
    bi = frame.picturePanel.getCurrentPicture();
    ArrayList<Integer> colorList = new ArrayList(); //frame.picturePanel.pictureColorValues(frame.picturePanel.getCurrentPicture());
    frame.picturePanel.setColorList(colorList);
    smallBi = PictureResize.resize(bi, Constants.RESIZED_PICTURE_SIZE, Constants.RESIZED_PICTURE_SIZE);
    LoadPictures.currentPicture(frame.picturePanel.getCurrentPicture());

    birthTribe(bi, colorList);

    Timer timer = new Timer();
    timer.scheduleAtFixedRate(new TimerTask() {
      

      @Override
      public void run()
      {
        
        generationspersec = totalgenerations - generationspersec;
        totalgenerations = totalmutations + totalcrossovers;
        frame.buttonPanel.updateGUIStats( totalgenerations,  totalmutations,  totalcrossovers,  totalgenomes,  generationspersec,  99,  99,  99,  99);
        System.out.println("1****\n****\n****\n");
        if (!tribe.isInterrupted())
        {
          System.out.println("2****\n****\n****\n");

            synchronized (tribe.genomes)
            {
              System.out.println("\tbest genome's hashcode " + tribe.genomes.get(0).hashCode());
              displayGenome(tribe.genomes.get(0));
            }
            if (sec == 59)
            {
              sec = 0;
              min++;
            }
            sec++;
            frame.buttonPanel.setTime(min, sec);
            // frame.buttonPanel.setGen(generations,hc_generations, ga_generations);
            System.out.println("3****\n****\n****\n");

        }
      }
    }, 0, 1000L);

    Timer statsFileTimer = new Timer();
    statsFileTimer.scheduleAtFixedRate(new TimerTask() {
      @Override
      public void run()
      {
        
        synchronized (tribe)
        {
          //totalgenerations = totalmutations + totalcrossovers;
          statsArray[0] = min;
          statsArray[1] = sec;
          statsArray[2] = totalgenerations;
          statsArray[3] = totalmutations;
          statsArray[4] = totalcrossovers;
          statsArray[5] = 0; //totaltribes;
          statsArray[6] = 0; //totalgenomes;
          statsArray[7] = 0; //bestfit;
          statsArray[8] = 0; //averagefitness;
          
        }
       

        try
        {
          new PrintStatsFile("file3").writeToFile(false,statsArray);
        }
        catch (IOException e)
        {
          e.printStackTrace();
        }
      }
    }, 0, 60000L);
  }

  /***************************************************************************************************
   * 
   * @param g
   **************************************************************************************************/
  public void displayGenome(Genome g)
  {
    
    frame.trianglePanel.displayGenome(g);
//    frame.buttonPanel.setFitness(g.getFitness(frame.picturePanel.getCurrentPicture(), 5));
    
  }

  /***************************************************************************************************
   * 
   * @param bImage
   * @param clist
   **************************************************************************************************/
  private void birthTribe(BufferedImage bImage, ArrayList<Integer> clist)
  {
    tribe = new Tribe("Tribe 1", bImage.getWidth(), bImage.getHeight(), bImage, clist);
    tribe.start();
    totalgenomes += tribe.genomes.size();
  }

  /***************************************************************************************************
   * 
   **************************************************************************************************/
  private void killTribe()
  {
    tribe.interrupt();
  }

  /***************************************************************************************************
   * 
   * @param bImage
   * @param clist
   **************************************************************************************************/
  private void restart(BufferedImage bImage, ArrayList<Integer> clist)
  {
    System.out.println("Restarting GA / HC with new picture");
    killTribe();
    birthTribe(bImage, clist);
  }

  /***************************************************************************************************
   * 
   * @return BufferedImage
   **************************************************************************************************/
  public static BufferedImage getCurrentPict()
  {
    return bi;
  }

  /***************************************************************************************************
   * 
   * @return
   **************************************************************************************************/
  public static BufferedImage getresizedPict()
  {
    return smallBi;
  }

  /***************************************************************************************************
   * 
   * Main starts the whole program..
   **************************************************************************************************/
  public static void main(String[] args)
  {
    new MainFrameController();
  }
}
