package genome.guicode;

import genome.Constants;
import genome.types.Genome;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

public class TableStats extends JFrame
{

  private JPanel topPanel;
  private static JTable table;
  private JScrollPane scrollPane;
  private final int COLS = 7;
  private final int ROWS = Constants.GENOME_SIZE+1;
  // Create columns names
  String columnNames[] =
  { "x1, y1", "x2, y2", "x3, y3", "red", "green", "blue", "alpha" };
  String dataValues[][] = new String[ROWS][COLS];
  

  public TableStats(Genome g)
  {   
    setTitle("Current Genome");
    setSize(400, 300);
    this.setLocation(200, 350);
    setBackground(Color.gray);
    topPanel = new JPanel();
    topPanel.setLayout(new BorderLayout());
    getContentPane().add(topPanel);
    table = new JTable(dataValues, columnNames);
    scrollPane = new JScrollPane(table);
    topPanel.add(scrollPane, BorderLayout.CENTER);
    this.setVisible(true);
    
    showTableData(g);
  }
  
  private void showTableData(Genome g)
  {
   
  

    for(int row = 0; row < ROWS; row++)
    {
       // "x1", "y1", "x2", "y2", "x3", "y3", "red", "green", "blue", "alpha"
      dataValues[row][0] = Integer.toString((g.getTriangles()[row].getPoint1().x)) +", "+ Integer.toString((g.getTriangles()[row].getPoint1().y));
      dataValues[row][1] = Integer.toString((g.getTriangles()[row].getPoint2().x)) +", "+ Integer.toString((g.getTriangles()[row].getPoint2().y));
      dataValues[row][2] = Integer.toString((g.getTriangles()[row].getPoint3().x)) +", "+ Integer.toString((g.getTriangles()[row].getPoint3().y));
      
      dataValues[row][3] = Integer.toString((g.getTriangles()[row].getRed()));
      dataValues[row][4] = Integer.toString((g.getTriangles()[row].getGreen()));
      dataValues[row][5] = Integer.toString((g.getTriangles()[row].getBlue()));
      dataValues[row][6] = Integer.toString((g.getTriangles()[row].getAlpha()));
     
    }

    table.repaint();
  }

}
