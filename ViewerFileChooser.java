

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;
import java.io.File;

public class ViewerFileChooser extends JFileChooser {
	/**
	 * create a ImageFileChooser in default path
	 * 
	 * @return void
	 */
	public ViewerFileChooser() {
		super();
		setAcceptAllFileFilterUsed(false);
		addFilter();
	}

	/**
	 * create a ViewerFileChooser in path we picked
	 * 
	 * @param currentDirectoryPath
	 * 
	 * @return void
	 */
	public ViewerFileChooser(String currentDirectoryPath) {
		super(currentDirectoryPath);
		setAcceptAllFileFilterUsed(false);
		addFilter();
	}

	/**
	 * image filter
	 * 
	 * @return void
	 */
	private void addFilter() {
		this.addChoosableFileFilter(new MyFileFilter(new String[] { ".JPG",
						".JPEG", ".JPE", ".JFIF" },
						"JPEG (*.JPG;*.JPEG;*.JPE;*.JFIF)"));
		this.addChoosableFileFilter(new MyFileFilter(new String[] { ".PGM" },
                                                "PGM (*.PGM)"));
	}

	class MyFileFilter extends FileFilter {
		// ��׺������
		String[] suffarr;
		// ����
		String decription;

		public MyFileFilter() {
			super();
		}

		/**
		 * Create a MyFileFilter
		 * 
		 * @param suffarr
		 *            String[]
		 * @param decription
		 *            String
		 * @return void
		 */
		public MyFileFilter(String[] suffarr, String decription) {
			super();
			this.suffarr = suffarr;
			this.decription = decription;
		}

		/**
		 *override boolean accept( File f )����
		 * 
		 * @paream f File
		 * @return boolean
		 */
                @Override
		public boolean accept(File f) {
			//if a file with the correct extend name, return true
			for (String s : suffarr) {
				if (f.getName().toUpperCase().endsWith(s)) {
					return true;
				}
			}
			//if it is a directory, return true or false
			return f.isDirectory();
		}

		/**
		 *get Description 
		 * 
		 * @return String
		 */
		public String getDescription() {
			return this.decription;
		}
	}

}