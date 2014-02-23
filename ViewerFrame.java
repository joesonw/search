

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.JButton;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.*;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Dimension;
import java.awt.BorderLayout;
import java.awt.Image;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.ArrayList;


public class ViewerFrame extends JFrame {
	//Set the picture area's width and height
	private int width = 800;
	private int height = 600;
	//Create a JLabel to store picture
	JLabel label = new JLabel();
        JLabel slabel = new JLabel();
        JScrollPane left = new JScrollPane();
        JScrollPane right = new JScrollPane();
        JLabel text = new JLabel();
	ViewerService service = ViewerService.getInstance();
        
	//MenuListener�����
	ActionListener menuListener = new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			service.menuDo(ViewerFrame.this, e.getActionCommand());
		}
	};

	/**
	 * Constructor������
	 */
	public ViewerFrame() {
		super();
		init();
	}

	/**
	 * Init
	 * 
	 * @return void
	 */
	public void init() {
		//Title��
                this.setLayout(new GridLayout(1,3,5,5));
		this.setTitle("CSE484 Image Retrieval");
		//Set width and height
		this.setPreferredSize(new Dimension(width, height));
		//Menu
		createMenuBar();
		//add toolbar and ScrollPanel into JFrame����
		this.add(new JScrollPane(label));
                this.add(new JScrollPane(slabel));
                this.add(text);
		//Set it to be visible           
		this.setVisible(true);
		this.pack();
	}

	/**
	 * Get JLabel
	 * 
	 * @return JLabel
	 */
	public JLabel getLabel() {
            return this.label;
	}
        public JLabel getsLabel(){
            return this.slabel;
        }
        public JScrollPane getLeft(){
            return this.left;
        }
        public JScrollPane getRight(){
            return this.right;
        }
        public JLabel getText(){
            return this.text;
        }
	/**
	 * Create MenuBar��
	 * 
	 * @return void
	 */

	public void createMenuBar() {
		//Create a JmenuBar to store menus
		JMenuBar menuBar = new JMenuBar();
		// string array, related to 2D array menuItemArr
		String[] menuArr = { "Files", "Tools" };
		String[][] menuItemArr = { { "Open", "Open external PGM file", "-", "Exit" },
				{ "Zoom in", "Zoom out", "-", "Previous", "Next","Image out of database","Image in database" } };
		//go through menuArr and menuItemArr to create menu
		for (int i = 0; i < menuArr.length; i++) {
			//create a new JMenu
			JMenu menu = new JMenu(menuArr[i]);
			for (int j = 0; j < menuItemArr[i].length; j++) {
				//if menuItemArr[i][j] is "-"
				if (menuItemArr[i][j].equals("-")) {
					//add separator
					menu.addSeparator();
				} else {
					//create a new JmenuItem���
					JMenuItem menuItem = new JMenuItem(menuItemArr[i][j]);
					menuItem.addActionListener(menuListener);
					//add it into menuItem����
					menu.add(menuItem);
				}
			}
			//Add menu to menuBar
			menuBar.add(menu);
		}
		//Set JmenuBar
		this.setJMenuBar(menuBar);
	}
}