package com.asal.project.classes;

public class NoteItem {
	
	private String noteTitle;
	private String noteDesc;
	
	public NoteItem(String noteTitle, String noteDesc) {
		super();
		this.noteTitle = noteTitle;
		this.noteDesc =  noteDesc	; 
	}
	
	public NoteItem(){
	
	}

	public String getNoteTitle() {
		return noteTitle;
	}

	public void setNoteTitle(String noteTitle) {
		this.noteTitle = noteTitle;
	}

	public String getNoteDesc() {
		return noteDesc;
	}

	public void setNoteDesc(String noteDesc) {
		this.noteDesc = noteDesc;
	}
}
