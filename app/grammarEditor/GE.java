package org.fleen.forsythia.app.grammarEditor;

import java.awt.CardLayout;
import java.awt.EventQueue;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.URLDecoder;

import org.fleen.forsythia.app.grammarEditor.editor_Generator.Editor_Generator;
import org.fleen.forsythia.app.grammarEditor.editor_Grammar.Editor_Grammar;
import org.fleen.forsythia.app.grammarEditor.editor_Jig.Editor_Jig;
import org.fleen.forsythia.app.grammarEditor.editor_Metagon.Editor_Metagon;
import org.fleen.forsythia.app.grammarEditor.project.ProjectGrammar;
import org.fleen.forsythia.app.grammarEditor.project.jig.ProjectJig;
import org.fleen.forsythia.app.grammarEditor.project.metagon.ProjectMetagon;
import org.fleen.forsythia.app.grammarEditor.sampleGrammars.SampleGrammars;
import org.fleen.forsythia.app.grammarEditor.util.Editor;

/*
 * ################################
 * ################################
 * ################################
 * 
 * FLEEN FORSYTHIA GRAMMAR EDITOR
 * Create and edit grammars for use in Forsythia production process
 * Generate sample Forsythia compostions
 * 
 * The app is an instance of GE
 * This class is the main class
 * It contains 
 *   references to subsystems
 *   methods for init and term
 *   utilities
 * We serialize the instance of this at exit, so that's our config
 * 
 * ################################
 * ################################
 * ################################
 */
public class GE implements Serializable{
  
  private static final long serialVersionUID=-2575411536818952885L;

  public static final String APPNAME="Fleen Forsythia Grammar Editor 0.2A";
  
  public static final String ABOUT=APPNAME+"\n by John Greene. \n fleen.org";
  
  /*
   * ################################
   * INIT
   * ################################
   */
 
  private void init(){
    initFocusGrammar();
    initUI();}
  
  /*
   * ################################
   * UI
   * ################################
   */

  //main ui. A frame. Holds editor uis
  public UIMain uimain;
  
  private boolean uiinitialized;
  
  private void initUI(){
    System.out.println("#### Q INIT ####");
    //init ui
    uiinitialized=false;
    EventQueue.invokeLater(new Runnable(){
      public void run(){
        try{
          uimain=new UIMain();
          editor_grammar=new Editor_Grammar();
          editor_metagon=new Editor_Metagon();
          editor_jig=new Editor_Jig();
          editor_generator=new Editor_Generator();
          editors=new Editor[]{editor_grammar,editor_metagon,editor_jig,editor_generator};
          for(Editor a:editors)
            uimain.paneditor.add(a.getUI(),a.getName());
          uiinitialized=true;
        }catch(Exception e){
          e.printStackTrace();}}});
    //wait a sec for the ui to finish initing
    while(!uiinitialized){
     try{ 
       Thread.sleep(1000,0);
     }catch(Exception x){
       x.printStackTrace();}}
    //open an editor
    setEditor(editor_grammar);}
  
  /*
   * ################################
   * EDITORS
   * One editor in use at a time
   * One editor for each aspect of GE
   * We show the Editor UIs one at a time, cardlayoutwise, in uimain
   * ################################
   */

  public Editor presenteditor=null;
  public Editor_Grammar editor_grammar;
  public Editor_Metagon editor_metagon;
  public Editor_Jig editor_jig;
  public Editor_Generator editor_generator;
  public Editor[] editors;
  
  public void setEditor(final Editor editor){
    if(presenteditor!=null)presenteditor.close();
      presenteditor=editor;
      CardLayout a=(CardLayout)uimain.paneditor.getLayout();
      String n=editor.getName();
      a.show(uimain.paneditor,n);
      uimain.setTitle(GE.APPNAME+" :: "+n);
      presenteditor.open();}
  
  /*
   * ################################
   * FOCUS GRAMMAR ELEMENTS
   * these are the grammar elements that we are focussing upon at any particular moment
   * grammar is never null
   * metagon and jig might be null
   * ################################
   */
  
  public ProjectGrammar focusgrammar=null;
  public ProjectMetagon focusmetagon=null;
  public ProjectJig focusjig=null;
  
  /*
   * the focus grammar may never be null
   */
  private void initFocusGrammar(){
    if(focusgrammar==null)
      focusgrammar=SampleGrammars.getPreferredGrammar();}
  
  /*
   * ################################
   * TERMINATE APP
   * write serialized instance of this class to local dir then exit
   * ################################
   */
  
  public void term(){
    System.out.println("GE TERMINATE");
    saveInstance(this);
    System.exit(0);}
  
  /*
   * ################################
   * UTIL
   * ################################
   */
  
  static final String GEINSTANCEFILENAME="GE.instance";
  
  public static final File getLocalDir(){
    String path=GE.class.getProtectionDomain().getCodeSource().getLocation().getPath();
    String decodedpath;
    try{
      decodedpath=URLDecoder.decode(path,"UTF-8");
    }catch(Exception x){
      throw new IllegalArgumentException(x);}
    File f=new File(decodedpath);
    if(!f.isDirectory())f=f.getParentFile();
    return f;}
  
  //load instance of this class from the local dir
  private static final GE loadInstance(){
    String pathtoconfig=GE.getLocalDir().getPath()+"/"+GEINSTANCEFILENAME;
    System.out.println("Loading instance : "+pathtoconfig);
    GE instance=null;
    try{
      FileInputStream a=new FileInputStream(pathtoconfig);
      ObjectInputStream b=new ObjectInputStream(a);
      instance=(GE)b.readObject();
      b.close();
    }catch(Exception e){
      System.out.println("Load instance failed.");}
    return instance;}
  
  //save instance of this class to local dir
  private static final void saveInstance(GE instance){
    String pathtoconfig=GE.getLocalDir().getPath()+"/"+GEINSTANCEFILENAME;
    System.out.println("saving instance : "+pathtoconfig);
    FileOutputStream fos;
    ObjectOutputStream oos;
    File file=new File(pathtoconfig);
    try{
      fos=new FileOutputStream(file);
      oos=new ObjectOutputStream(fos);
      oos.writeObject(instance);
      oos.close();
    }catch(IOException x){
      System.out.println("Save instance failed.");
      x.printStackTrace();}}
  
  /*
   * ################################
   * MAIN
   * ################################
   */
  
  public static GE ge;
  
  /*
   * get local dir
   * load serialized instance of GE
   * if serialized instance fails to load then create a new one
   */
  public static final void main(String[] a){
    ge=loadInstance();
    if(ge==null){
      System.out.println("constructing instance of GE");
      ge=new GE();
      ge.init();}}
  
}
