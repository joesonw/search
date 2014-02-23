

import java.util.Iterator;
import java.util.Set;
import javax.swing.ImageIcon;
import java.awt.Image;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import javax.swing.filechooser.FileFilter;
import java.util.List;
import java.util.ArrayList;
import java.util.TreeMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import javax.swing.JLabel;
import javax.swing.JScrollPane;

public class ViewerService {
	private static ViewerService service = null;
	//create a new ViewerFileChooser
	private ViewerFileChooser fileChooser = new ViewerFileChooser();
        //create a new ViewerFileChooser for result images
        private ViewerFileChooser rFileChooser = new ViewerFileChooser();
	// Zoom in and Zoom out factor
	private double range = 0.2;
	// Current Directory
	private File currentDirectory = null;
	// a list of file under this directory
	private List<File> currentFiles = null;
	// currentFile
	private File currentFile = null;
        // currentSearchResult
        private File theTop = null;
        // currentSearchResultsList
        private List<File> theTops = null;
        //SearchBuilder
        String index_ = "indexfile";
        String terc_ = "esp.trec";
        SearchBuilder searcher = new SearchBuilder(index_,terc_);
        // image base
        private List<File> base = null;
        // text
        private List<String> textS = null;
        Process t;
	Scanner cout;
	BufferedWriter cin;

	/**
	 * constructor锟斤拷锟斤拷
	 */
	private ViewerService() {
                    // SearchBuilder

                        //this.fileChooser.setFileView(new ImageFileView());
            try {
                        t=Runtime.getRuntime().exec("build-new.exe");
			System.out.println("preparing");
                        System.out.println("path found");
			BufferedReader out=new BufferedReader(new InputStreamReader(t.getInputStream()));
			cin=new BufferedWriter(new OutputStreamWriter(t.getOutputStream()));
			String line="a";
			cout=new Scanner(out);
                        System.out.println("still prep");
			System.out.println(cout.nextLine());
			System.out.println("ready");
            } catch (Exception e) {
                System.err.println(e.toString());
            }
                        this.fileChooser.setAccessory(new ImagePreview(this.fileChooser));
	}

	/**
	 * get a instance of ViewerService()
	 * 
	 * @return ViewerService
	 */
	public static ViewerService getInstance() {
		if (service == null) {
			service = new ViewerService();
		}
		return service;
	}

	/**
	 * open file
	 * 
	 * @param frame
	 *            ViewerFrame
	 * @return void
	 */
	public void open(ViewerFrame frame) {
		//if open
                this.fileChooser.setCurrentDirectory(new File("img"));
		if (fileChooser.showOpenDialog(frame) == ViewerFileChooser.APPROVE_OPTION) {
			//assign this file锟街�
			this.currentFile = fileChooser.getSelectedFile();
			//get the path of this file
			String name = this.currentFile.getPath();
			//get the current directory
			File cd = fileChooser.getCurrentDirectory();
			//if directory has changed
			if (cd != this.currentDirectory || this.currentDirectory == null) {
                            
				FileFilter[] fileFilters = fileChooser
						.getChoosableFileFilters();
				File files[] = cd.listFiles();
				this.currentFiles = new ArrayList<File>();
				for (File file : files) {
					for (FileFilter filter : fileFilters) {
						//if this file is a image file
						if (filter.accept(file)) {
							//add this file into currentFile
							this.currentFiles.add(file);
						}
					}
				}
			}
                        //display this image
			ImageIcon icon = new ImageIcon(name);
                        frame.getLabel().setText("");
			frame.getLabel().setIcon(icon);
		}
	}
        	public void openEx(ViewerFrame frame) {
		//if open
                this.fileChooser.setCurrentDirectory(new File("."));
		if (fileChooser.showOpenDialog(frame) == ViewerFileChooser.APPROVE_OPTION) {
			//assign this file锟街�
			this.currentFile = fileChooser.getSelectedFile();
			//get the path of this file
			String name = this.currentFile.getPath();
			//get the current directory
			File cd = fileChooser.getCurrentDirectory();
			//if directory has changed
			if (cd != this.currentDirectory || this.currentDirectory == null) {
                            
				FileFilter[] fileFilters = fileChooser
						.getChoosableFileFilters();
				File files[] = cd.listFiles();
				this.currentFiles = new ArrayList<File>();
				for (File file : files) {
					for (FileFilter filter : fileFilters) {
						//if this file is a image file
						if (filter.accept(file)) {
							//add this file into currentFile
							this.currentFiles.add(file);
						}
					}
				}
			}
                        frame.getLabel().setIcon(null);
			frame.getLabel().setText("An external PGM file: " + this.currentFile.getPath());
		}
	}
	/**
	 * Zooming
	 * 
	 * @param frame
	 *            ViewerFrame
	 * @return void
	 */
	public void zoom(ViewerFrame frame, boolean isEnlarge) {
		//get the factor of zooming
		double enLargeRange = isEnlarge ? 1 + range : 1 - range;
		//get this current image
		ImageIcon icon = (ImageIcon) frame.getLabel().getIcon();
		if (icon != null) {
			int width = (int) (icon.getIconWidth() * enLargeRange);
			//get the new image (after zooming in or out)
			ImageIcon newIcon = new ImageIcon(icon.getImage()
					.getScaledInstance(width, -1, Image.SCALE_DEFAULT));
			//change image
			frame.getLabel().setIcon(newIcon);
		}
	}

	/**
	 * Previous
	 * 
	 * @param frame
	 *            ViewerFrame
	 * @return void
	 */
	public void last(ViewerFrame frame) {
		//if there is other image
		if (this.theTops != null && !this.theTops.isEmpty()) {
			int index = this.theTops.indexOf(this.theTop);
			//open the previous one
			if (index > 0) {
				File file = (File) this.theTops.get(index - 1);
				ImageIcon icon = new ImageIcon(file.getPath());
				frame.getsLabel().setIcon(icon);
                                frame.getText().setText("<html>the Top " + String.valueOf(this.theTops.indexOf(file)) + " is<br/> "+ this.textS.get(this.theTops.indexOf(file)) + "<br/></html>");
				this.theTop = file;
			}
		}
	}

	/**
	 *next
	 * 
	 * @param frame
	 *            ViewerFrame
	 * @return void
	 */
	public void next(ViewerFrame frame) {
		//if there is other image
		if (this.theTops != null && !this.theTops.isEmpty()) {
			int index = this.theTops.indexOf(this.theTop);
			//open the previous one
			if (index >= 0) {
				File file = (File) this.theTops.get(index + 1);
				ImageIcon icon = new ImageIcon(file.getPath());
				frame.getsLabel().setIcon(icon);
                                frame.getText().setText("<html>the Top " + String.valueOf(this.theTops.indexOf(file)) + " is<br/> "+ this.textS.get(this.theTops.indexOf(file)) + "<br/></html>");
				this.theTop = file;
			}
		}
	}

        /**
         * Search Action
         */
        public void Search(ViewerFrame frame) {
            //do search 
            System.out.println("Do search"); 
            TreeMap<String,Float> result = null;
            System.out.println(this.currentFile.getName());
            result = this.searcher.searchByName(this.currentFile.getName());
            System.out.println(result);
            if (result != null){
                Set set = result.entrySet();
                Iterator it = set.iterator();
                this.theTops = new ArrayList<File>();
                this.textS = new ArrayList<String>();
                for(int i = 0; i<11; i++){
                    Map.Entry<String,Float> entry =(Entry<String, Float>) it.next();
                    String Name = "img\\" + entry.getKey();
                    System.out.println(Name + " " + entry.getValue());
                    File f = new File(Name);
                    String Info = entry.getKey() + " It's Score is: " + String.valueOf(entry.getValue());
                    this.theTops.add(f);  
                    this.textS.add(Info);
                }
                this.theTop = theTops.get(0);
                System.out.println(this.theTop.getName());
                ImageIcon icon = new ImageIcon(this.theTop.getPath());
                frame.getsLabel().setIcon(icon);
                frame.getText().setText("<html>the Top " + String.valueOf(this.theTops.indexOf(theTop)) + " is<br/> "+ this.textS.get(this.theTops.indexOf(theTop)) + "<br/></html>");
            }
        }
        public void OutSearch(ViewerFrame frame) {
		try {
			
			
			System.out.println(this.currentFile.getPath());
			cin.write(this.currentFile.getPath());
			cin.write("\n");
			cin.flush();
			System.out.println(cout.nextLine());
			String line=cout.nextLine();
			System.out.println(line);
			TreeMap<String,Float> r=this.searcher.searchByRaw(line);
		
                        if (r != null){
                            Set set = r.entrySet();
                            Iterator it = set.iterator();
                            this.theTops = new ArrayList<File>();
                            this.textS = new ArrayList<String>();
                            for(int i = 0; i<11; i++){
                                Map.Entry<String,Float> entry =(Entry<String, Float>) it.next();
                                String Name = "img\\" + entry.getKey();
                                System.out.println(Name + " " + entry.getValue());
                                File f = new File(Name);
                                String Info = entry.getKey() + " It's Score is: " + String.valueOf(entry.getValue());
                                this.theTops.add(f);  
                                this.textS.add(Info);
                            }
                             this.theTop = theTops.get(0);
                             System.out.println(this.theTop.getName());
                             ImageIcon icon = new ImageIcon(this.theTop.getPath());
                             frame.getsLabel().setIcon(icon);
                             frame.getText().setText("<html>the Top " + String.valueOf(this.theTops.indexOf(theTop)) + " is<br/> "+ this.textS.get(this.theTops.indexOf(theTop)) + "<br/></html>");
                        }
		} catch (Exception e) {
			System.err.println(e.toString());
		}
		System.out.println("done");
        }
	/**
	 * put them and menu together
	 * 
	 * @param frame
	 *            ViewerFrame
	 * @param cmd
	 *            String
	 * @return void
	 */
	public void menuDo(ViewerFrame frame, String cmd) {
		//open
		if (cmd.equals("Open")) {
			open(frame);
		}
                //open external file
                if (cmd.equals("Open external PGM file")){
                    openEx(frame);
                }
		//Zoom in
		if (cmd.equals("Zoom in")) {
			zoom(frame, true);
		}
		//Zoom out
		if (cmd.equals("Zoom out")) {
			zoom(frame, false);
		}
		//previous
		if (cmd.equals("Previous")) {
			last(frame);
		}
		//next
		if (cmd.equals("Next")) {
			next(frame);
		}
                //search
                if (cmd.equals("Image in database")){
                        Search(frame);
                }
                if (cmd.equals("Image out of database")){
                        OutSearch(frame);
                }
		//exit
		if (cmd.equals("Exit(X)")) {
			System.exit(0);
		}
                
	}
}