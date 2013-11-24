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
 * 11/4/2013
 * This class assembles the gui and handles the action listeners for the main
 * search and add tab.
 */

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.*;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class searchPanel extends JPanel{

	private static final long serialVersionUID = 1L;
	private DisplayField listField = new DisplayField();
	private itemPane itemInfo = null;
	private DisplayWindow mainWindow;
	private Entry currentEntry = new Entry("", null);
	private boolean listChange = false;
	private int listSize = 0;
	private String[] remainingList = null;
	private JScrollPane scroll = null;
	private JList<String> list = null;
	private boolean skipSelection = true;
	private boolean selectionMade = false;
	
	private JTextField textEntry = new JTextField(15);
	private JPopupMenu pop = new JPopupMenu();
	private JButton add = new JButton ("Add");
	private JButton remove = new JButton ("Remove");
	private JLabel text = new JLabel("Enter an item below, use the add button or return key to add it to your list.");
	private JLabel text2 = new JLabel(" ");
	
	private ActionClass action = new ActionClass();
	private KeyClass key = new KeyClass();
	private SelectionHandler select = new SelectionHandler();
	private filer listManager;
	private BrowserPanel browser;
	private JPanel centerPanel = new JPanel();
	private JPanel buttonPanel = new JPanel();
	private GridBagConstraints c = new GridBagConstraints();
	

	public searchPanel(filer a, DisplayWindow mainWindow, itemPane c) {
		this.setLayout(new BorderLayout());
		this.mainWindow = mainWindow;
		itemInfo = c;
			
		listManager = a;
		itemInfo.setFiler(a);
		listManager.setDisplayField(listField);
//		listManager.setItemPane(c);
		
		GridBagLayout layout = new GridBagLayout();
		layout = createLayout(layout);
		
		//set up text and buttons for the bottom of the tab
		pop.setFocusable(false);
		pop.setPopupSize(new Dimension(200, 100));
		add.addActionListener(action);
		remove.addActionListener(action);
		textEntry.addKeyListener(key);
		text2.setFont(new Font("Italics", Font.ITALIC, 12));
		buttonPanel.setLayout(layout);
		buttonPanel.add(text);
		buttonPanel.add(textEntry);
		buttonPanel.add(add);
		buttonPanel.add(remove);
		buttonPanel.add(text2);
		
		listField.setBorder(BorderFactory.createEtchedBorder(1));
		centerPanel.setLayout(new BorderLayout());
		centerPanel.add(listField, BorderLayout.CENTER);
		centerPanel.add(itemInfo, BorderLayout.EAST);

		this.add(buttonPanel, BorderLayout.SOUTH);
		this.add(centerPanel, BorderLayout.CENTER);

	}
	
	public GridBagLayout createLayout(GridBagLayout layout){
		
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 0;
		c.gridwidth = GridBagConstraints.REMAINDER;
		c.weightx = 1;
		c.weighty = 1;
		c.anchor = GridBagConstraints.NORTHWEST;
		layout.setConstraints(text, c);
		
		c.fill = GridBagConstraints.BOTH;
		c.gridwidth = 1;
		c.gridy = 1;
		c.weightx = 5;
		layout.setConstraints(textEntry, c);

		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 1;
		c.weightx = 1;
		c.ipadx = 0;
		layout.setConstraints(add, c);
		
		c.gridx = 2;
		layout.setConstraints(remove, c);
		
		c.gridx = 0;
		c.gridy = 2;
		c.gridwidth = GridBagConstraints.REMAINDER;
		layout.setConstraints(text2, c);
		
		return layout;
	}

	public filer getFiler(){
		return listManager;
	}
	
	public void saveFiles(){
		if(DisplayWindow.readOnly)
			listManager.saveFiles(1);
		else
			listManager.saveFiles(3);

	listChange = true;
	textEntry.setSelectionStart(0);
	textEntry.setSelectionEnd(textEntry.getText().length());
		
	}
	
	public boolean checkDuplicates(String item){
		switch(DisplayWindow.language){
		
		//washbasin handler in english
		case 0:
		case 1:
			if( (item.startsWith("w") || item.startsWith("W"))){
				if(	currentEntry.normalizeText(item).compareTo("washbasin") == 0){
					new ItemCheckDialog(listManager, mainWindow, itemInfo, 0);
					return true;
				}
			}
			return false;
			
		//lanterna cinese handler in italian
		case 3:
			if( (item.startsWith("l") || item.startsWith("L"))){
				if(	currentEntry.normalizeText(item).compareTo("lanternacinese") == 0){
					new ItemCheckDialog(listManager, mainWindow, itemInfo, 1);
					return true;
				}
			}
			return false;
		
		//plate armor/ samurai shirt handler in japanese
		case 6:
			if( item.startsWith("\u304b") ){
				if(	item.compareTo("\u304b\u3063\u3061\u3085\u3046") == 0){
					new ItemCheckDialog(listManager, mainWindow, itemInfo, 2);
					return true;
				}
			}
			return false;
		default:
			return false;			
		}
	}
	
	public void setBrowser(BrowserPanel a){
		browser = a;
	}
	
	//creates a list of Strings that start with the text in the textEntry field and adds it to the popupmenu
	public void populatePopup(){
		list = new JList<String>();
		TreeSet<String> sList = new TreeSet<String>();
		list.addListSelectionListener(select);
		if( scroll != null)
			pop.remove(scroll);
		pop.setVisible(false);
		scroll = new JScrollPane(list);
		
		int i = 0;
		if( listSize == 0){
			for(Entry a: listManager.getList().values()){
				if( a.searchName.startsWith(currentEntry.normalizeText(textEntry.getText()))){
				sList.add(a.displayName);
				i++;
				}
			}
			
		} else {
			for(String a: remainingList){
				if(currentEntry.normalizeText(a).startsWith(currentEntry.normalizeText(textEntry.getText()))){
					sList.add(a);
					i++;
				}
			}			
		}
		remainingList = sList.toArray(new String[i]);
		list.setListData(remainingList);
		listSize = i;
		pop.add(scroll);
		pop.setVisible(true);
	}


	private class KeyClass implements KeyListener{
		public void keyPressed(KeyEvent e){
			String item = "";
			Entry current;
			int result;
			//perform "add word" function on enter, refresh search result and highlight text
			if(e.getKeyCode() == KeyEvent.VK_ENTER){
				if(list.getSelectedIndex() != -1){

					textEntry.setText(list.getSelectedValue());
					pop.setVisible(false);
					list.setSelectedIndex(-1);
					currentEntry = listManager.searchListControl(new Entry(textEntry.getText(), null));
					itemInfo.update(currentEntry);
					return;
				}
				listChange = true;
				if(textEntry.getText() != null)
					item = textEntry.getText();
				
				if( checkDuplicates(item)){
					browser.update();
					return;
				}
				
				//get info from itemPane if saving attributes
				if(!DisplayWindow.readOnly && listManager.getUserSize() != 0){
					current = itemInfo.getEntry();
				} else
					current = new Entry(item, null);

				result = listManager.addWord(current);

				if (result == 2 ) {
					text2.setForeground(new Color(5, 128, 15));
					text2.setText(item + " successfully added to the list");
					BrowserPanel.owned++;
					listManager.searchListControl(new Entry(item, null));
					pop.setVisible(false);
					saveFiles();
				} else{
					text2.setForeground(Color.RED);
					pop.setVisible(false);

					if(result == 1)
						text2.setText(item + " was not found in the main list");
					else
						text2.setText(item + " is already in the user list");
						
					saveFiles();
				}
				browser.update();
				
					

			}
		}

		@Override
		public void keyTyped(KeyEvent e) {
			//dynamic search didn't work well under this method, after the first letter, it was always 1 letter behind
		}

		@Override
		public void keyReleased(KeyEvent e) {
			//perform search as user types
			if( !listChange )
				text2.setText(" ");
			listChange = false;
			if(listSize != 0){
			if( e.getKeyCode() == KeyEvent.VK_UP ){
				if( list.getSelectedIndex() == -1)
					list.setSelectedIndex(0);
				else if( list.getSelectedIndex() == 0 )
					list.setSelectedIndex(listSize-1);
				else
					list.setSelectedIndex(list.getSelectedIndex()-1);
				list.ensureIndexIsVisible(list.getSelectedIndex());
				skipSelection = true;
				return;
			} else if( e.getKeyCode() == KeyEvent.VK_DOWN ){
				if( list.getSelectedIndex() == -1)
					list.setSelectedIndex(0);
				else if( list.getSelectedIndex() == listSize-1 )
					list.setSelectedIndex(0);
				else
					list.setSelectedIndex(list.getSelectedIndex()+1);
				list.ensureIndexIsVisible(list.getSelectedIndex());
				skipSelection = true;
				return;
			}
			}
			if(listManager.getUserSize() == 0 )
				return;
			if(listSize != 1 && !selectionMade)
				populatePopup();
			
			if(/*e.getKeyCode() != KeyEvent.VK_ENTER &&*/ textEntry.getText() != null){
				currentEntry = listManager.searchListControl(new Entry(textEntry.getText(), null));
				itemInfo.update(currentEntry);
				if( e.getKeyCode() == KeyEvent.VK_BACK_SPACE && textEntry.getText().compareTo("") == 0){
					listSize = 0;		
					pop.setVisible(false);
					return;
				}
				if( e.getKeyCode() == KeyEvent.VK_ENTER)
					return;
			}
			if(selectionMade){
				selectionMade = false;
				return;
			}
			pop.show(textEntry, textEntry.getX(), textEntry.getY()+10 );

		}
	}
	
	//handles the popupmenu's jlist
	private class SelectionHandler implements ListSelectionListener{
		public void valueChanged(ListSelectionEvent e){
			if( skipSelection){
				skipSelection = false;
				return;
			}
			textEntry.setText(list.getSelectedValue());
			pop.setVisible(false);
			list.setSelectedIndex(-1);
			currentEntry = listManager.searchListControl(new Entry(textEntry.getText(), null));
			itemInfo.update(currentEntry);
			selectionMade = true;
		}
	}

	private class ActionClass implements ActionListener{
		public void actionPerformed(ActionEvent e){
			Entry searchWord = null;
			int result;

			if( textEntry.getText() == null)
				return;

			searchWord = new Entry(textEntry.getText(), null);

			//same function as pressing return
			if( e.getSource() == add ){
				
				//get info from itemPane if saving selected attributes
				if( listManager.getUserSize() != 0 && !DisplayWindow.readOnly )
					searchWord = itemInfo.getEntry();
				
				if( checkDuplicates(searchWord.displayName) ){
					browser.update();
					return;
				}
				
				result = listManager.addWord(searchWord);
				if( result == 2 ){
					pop.setVisible(false);
					text2.setForeground(new Color(5, 128, 15));
					text2.setText(searchWord.displayName + " successfully added to the list");
					BrowserPanel.owned++;
					saveFiles();
					currentEntry = listManager.searchListControl(searchWord);
				} else {
					pop.setVisible(false);
					text2.setForeground(Color.RED);
					if(result == 1)
						text2.setText(searchWord.displayName + " was not found in the main list");
					else
						text2.setText(searchWord.displayName + " is already in the user list");
						
					saveFiles();

				}
				itemInfo.update(currentEntry);
			}

			//removes word from masterIndex if not readOnly, switches to notOwned if it is readOnly
			if( e.getSource() == remove){

				if( listManager.removeWord(searchWord) ){
					text2.setForeground(new Color(5, 128, 15));
					text2.setText(searchWord.displayName + " successfully removed from the list");
					pop.setVisible(false);
					BrowserPanel.owned--;
					if(!DisplayWindow.readOnly)
						BrowserPanel.total--;
					saveFiles();
				} else {
					pop.setVisible(false);
					text2.setForeground(Color.RED);
					text2.setText(searchWord.displayName + " not found in the list");
				}
				if(listManager.getUserSize() == 0 )
					return;
				currentEntry = listManager.searchListControl(searchWord);
				itemInfo.update(currentEntry);
			}
			browser.update();
		}
	}

}
