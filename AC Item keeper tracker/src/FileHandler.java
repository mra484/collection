/**Animal Crossing Item Cataloger
 * Copyright(C) 2013 Mark Andrews
 * 
 *   Animal Crossing Item Cataloger is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   Animal Crossing Item Cataloger is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *   
 *   You should have received a copy of the GNU General Public License
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *   
 *11/10/2013
 * Class handles all file related operations.  masterIndex.txt and userIndex.txt use the
 * UTF-8 character set, settings.ini uses ANSI
 */

import java.awt.Point;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.Scanner;


public class FileHandler{

	private FileInputStream fis;
	private BufferedReader unicodeReader;
	private FileOutputStream fos;
	private BufferedWriter unicodeWriter;
	private File input;
	private File input2;
	private PrintStream fileWriter;
	private Scanner fileReader;
	
	private filer listManager = null;
	private DisplayWindow mainWindow = null;
	
	public FileHandler(filer a, DisplayWindow b) {

			listManager = a;
			mainWindow = b;
			
			readSettings();
			
			openFileRead("masterIndex.txt");
			readReferenceList();
			
			openFileRead("userIndex.txt");
			readUserList();
	
//			saveReference();
	}
	
    private void readSettings(){
        String read;
        String[] readValues;
        double x = 0, y = 0;
        openPlainFileRead("settings.ini");
        if(!fileReader.hasNext()){
                fileReader.close();
                return;
        }
        try{
                //parse settings file
                while(fileReader.hasNext()){
                        read = fileReader.nextLine();
                        readValues = read.split("=");
                        
                        if(readValues[0].compareTo("language") == 0)
                                DisplayWindow.language = Integer.parseInt(readValues[1]);
                        else if(readValues[0].compareTo("readOnly") == 0)
                                DisplayWindow.readOnly = Boolean.parseBoolean(readValues[1]);
                        else if(readValues[0].compareTo("defaultOwned") == 0)
                                DisplayWindow.defaultOwned = Boolean.parseBoolean(readValues[1]);
                        else if(readValues[0].compareTo("DisableAutofinish") == 0)
                                DisplayWindow.popup = Boolean.parseBoolean(readValues[1]);
                        else if(readValues[0].compareTo("DisableListWarning") == 0)
                                DisplayWindow.listWarning = Boolean.parseBoolean(readValues[1]);
                        else if(readValues[0].compareTo("mainWindow.X") == 0)
                                x = Double.parseDouble(readValues[1]);
                        else if(readValues[0].compareTo("mainWindow.Y") == 0)
                                y = Double.parseDouble(readValues[1]);                                        
                }
                DisplayWindow.windowPos = new Point();
                DisplayWindow.windowPos.setLocation(x,y);
                
        } catch (Exception e){
                System.out.println("Error reading from settings.ini");
        }
        
        openFileRead("userIndex.txt");
        try {
                
                //check the start of the userIndex for a language value
                read = unicodeReader.readLine();
                if( read.contains("@") ){
                        readValues = read.split(" ");
                        DisplayWindow.language = Integer.parseInt(readValues[1]);
                }
                unicodeReader.close();
        } catch (Exception e) {
                System.out.println("Problem reading from userIndex.txt in readSettings().");
        }
                        
        fileReader.close();
        
}
	
	private void openFileRead(String fileName) {

		try
		{
			fis = new FileInputStream(fileName);
			unicodeReader = new BufferedReader(new InputStreamReader(fis, "UTF-8"));
		} catch (FileNotFoundException e) {
			System.out.println("Unable to open " + fileName + " for reading");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void openPlainFileRead(String fileName){
		input = new File(fileName);
		try {
			fileReader = new Scanner(input);
		} catch (FileNotFoundException e) {
			try {
				input.createNewFile();
				fileReader = new Scanner(input);
			} catch (IOException e1) {
				System.exit(-1);
			}
		}
		
	}
	private void openFileWrite(String fileName) {
		try
		{
			fos = new FileOutputStream(fileName);
			unicodeWriter = new BufferedWriter(new OutputStreamWriter(fos, "UTF-8"));
		}
		catch (FileNotFoundException ex)
		{
			System.out.println("Unable to open " + " for writing");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void openPlainFileWrite(String fileName){
		input = new File(fileName);
		try {
			fileWriter = new PrintStream(input);
		} catch (FileNotFoundException e) {
			System.out.println("Unable to open " + fileName + " for writing");
		}
		
	}

	private void readReferenceList(){

		Entry newEntry;
		String a, b, c, d, e, f, g, h;
		String[] splitArray, intArray;
		byte type, series, set, theme, clothes, style, furniture, catalog;

		try {
			while(true){
				a = unicodeReader.readLine();
				if(a == null)
					break;

				//split array into clusters containing attribute values, and names
				//split the values array into individual values
				splitArray = a.split("\"");
				intArray = splitArray[0].split(" ");
				type = Byte.parseByte(intArray[0]);
				series = Byte.parseByte(intArray[1]);
				set = Byte.parseByte(intArray[2]);
				theme = Byte.parseByte(intArray[3]);
				clothes = Byte.parseByte(intArray[4]);
				style = Byte.parseByte(intArray[5]);
				furniture = Byte.parseByte(intArray[6]);
				catalog = Byte.parseByte(intArray[7]);
				a = splitArray[1];
				b = splitArray[3];
				c = splitArray[5];
				d = splitArray[7];
				e = splitArray[9];
				f = splitArray[11];
				g = splitArray[13];
				h = splitArray[15];
				newEntry = new Entry(a, b, c, d, e, f, g, h, type, series, set, theme, clothes, style, furniture, catalog, null);
				listManager.getList().put(newEntry.searchName, newEntry);
				listManager.incTotalItems();
			}
			unicodeReader.close();
		} catch (IOException e1) {
			System.out.println("Unable to read from masterIndex.txt in readReferenceList()");
		}
	}
//	private byte readValue(String a){
//		
//	}
	
	private void readUserList(){
		String name;
		Entry prev = null;
		Entry current = null;
		boolean catalog;
		
		try {
			while(true){
				name = unicodeReader.readLine();

				if( name == null )
					break;
//				catalog = (name.endsWith("*" ) ? false : true );
//				name = name.replace("*", "");

				//check for the current item in the index, if it isn't listed, print it to the console and move to the next item
				current = new Entry(name, null);
				if(listManager.getList().containsKey(current.searchName)){
					current = listManager.getList().get(current.searchName);
				} else{
					listManager.getMissingList().add(current.displayName);
					System.out.println(current.displayName);
					continue;
				}

				//use with specific lists to edit item properties
				//current.setStyle((byte)9);
//				current.catalog = catalog;
				
				//set link for searches, owned for browsing, and inc user size for itemSorter
				current.addPrev(prev);
				current.setOwned(true);
				listManager.incUserSize();
				
				//take separate action if this is the first item to be read
				if( prev != null )
					prev.addNext(current);
				else{
					listManager.setHead(current);
					current.setHead(true);
				}
				prev = current;

			}
			unicodeReader.close();
			
		} catch (IOException e) {
			System.out.println("Unable to read from userIndex.txt in readUserList()");
		}
	
		listManager.setLast(current);
		if( current != null)
			current.setLast(true);
	}
	
	public void saveReference(){
		
		//masterIndex will save all information except whether it is owned or not
		openFileWrite("masterIndex.txt.temp");
		try {
			for(Entry a: listManager.getList().values()){
				unicodeWriter.write(a.toString(), 0, a.toString().length());
				unicodeWriter.newLine();
			}
			unicodeWriter.flush();
			unicodeWriter.close();
			fos.close();
			} catch (IOException e) {
				System.out.println("Unable to save to masterIndex.txt");
			}
		input2 = new File("masterIndex.txt");
		input2.delete();
		input = new File("masterIndex.txt.temp");
		input.renameTo(new File("masterIndex.txt"));
		
	}
	
	public void saveUser(){

		//userIndex will save the name of owned items only in the current language of the program
		openFileWrite("userIndex.txt.temp");
		try {
			unicodeWriter.write(String.format("%s %d", "@", DisplayWindow.language));
			unicodeWriter.newLine();
			for(Entry a: listManager.getList().values()){
				if(a.getOwned()){
					unicodeWriter.write(a.displayName);
					unicodeWriter.newLine();					
				}
			}
			unicodeWriter.flush();
			unicodeWriter.close();
			fos.close();
			} catch (IOException e) {
				System.out.println("Unable to save to userIndex.txt");
			}
		input2 = new File("userIndex.txt");
		input2.delete();
		input = new File("userIndex.txt.temp");
		input.renameTo(new File("userIndex.txt"));
		
		
	}
	public void saveSettings(){
		
		//System settings saved are the language, read only, display owned items checkbox status, and the x y coordinates of the main window
		openPlainFileWrite("settings.ini.temp");
		fileWriter.printf("language=%d\nreadOnly=%s\ndefaultOwned=%s\nmainWindow.X=%d\nmainWindow.Y=%d\n" + 
				"DisableAutofinish=%s\nDisableListWarning=%s", DisplayWindow.language, DisplayWindow.readOnly,
				DisplayWindow.defaultOwned, mainWindow.getX(), mainWindow.getY(), DisplayWindow.popup, DisplayWindow.listWarning);
		fileWriter.flush();
		fileWriter.close();
		input2 = new File("settings.ini");
		System.out.println(input2.delete());
		input = new File("settings.ini.temp");
		input.renameTo(new File("settings.ini"));
	}
}
