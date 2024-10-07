package uno.due;

import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xslf.usermodel.XSLFSlide;
import org.apache.poi.xslf.usermodel.XSLFTextBox;
import org.apache.poi.xslf.usermodel.XSLFTextParagraph;
import org.apache.poi.xslf.usermodel.XSLFTextRun;

import java.awt.Color;
import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JFileChooser;

public class App {

    public static void main(String[] args) {
    	
    	List<String> lista_righe_file=new ArrayList<String>();
    	
    	final JFileChooser fc = new JFileChooser();
    	int returnVal = fc.showOpenDialog(null);

        if (returnVal == JFileChooser.APPROVE_OPTION) 
        {
            File file = fc.getSelectedFile();
            String filePath=file.getAbsolutePath();
            //This is where a real application would open the file.
            //log.append("Opening: " + file.getName() + "." + newline);
    	
    	
	    	//String filePath = new File("./output_chatgpt.txt").getAbsolutePath();
	        
	        try (BufferedReader br = new BufferedReader(new FileReader(filePath, StandardCharsets.UTF_8))) 
	        {
	            String line;
	            while ((line = br.readLine()) != null) 
	            {
	            	lista_righe_file.add(line);
	            }
	        } 
	        catch (IOException e)
	        {
	            e.printStackTrace();
	        }
	    	
	        
	        List<Slide> lista_slide=new ArrayList<Slide>();
	        
	        if(!lista_righe_file.isEmpty())
	        {
	        	int numero_slide=0;
	        	
		        for(int i=0;i<lista_righe_file.size();i++)
		        {
		        	if(lista_righe_file.get(i).contains("Slide"))
		        	{
		        		Slide slide=new Slide();
		        		String titolo_grezzo=lista_righe_file.get(i+1);
		        		titolo_grezzo=titolo_grezzo.replace("*", "");
		        		String[] titolo_grezzo_parse=titolo_grezzo.split(":");
		        		slide.titolo=titolo_grezzo_parse[1];
		        		System.out.println(slide.titolo);
		        		for(int j=i+2;j<lista_righe_file.size();j++)
		        		{
		        			if(!lista_righe_file.get(j).contains("Slide"))
		    	        	{
		        				if(!lista_righe_file.get(j).contains("```"))
		        				slide.righe_slide.add(lista_righe_file.get(j).replace("*", ""));
		        				if(j==(lista_righe_file.size()-1))
			        			{
			        				lista_slide.add(slide);
			        				for(int k=slide.righe_slide.size()-1;k>1;k--)
			        				{
			        					System.out.println(k);
			        					if(slide.righe_slide.get(k).equals("---"))
			        					{
			        						break;
			        					}
			        					else
			        					{
			        						slide.righe_slide.remove(k);
			        						
			        					}
			        				}
			        			}
		    	        	}
		        			else
		        			{
		        				i=j-1;
		        				lista_slide.add(slide);
		        				break;
		        			}
		        		}
		        		
		        	}
		        		numero_slide++;
		        }
		    	//System.out.println(numero_slide);
		    	//System.exit(0);
		    }
	        else
	        {
	        	System.out.println("file vuoto");
		    	//System.exit(0);
	        }
	        XMLSlideShow ppt =null;
	        // Creiamo una nuova presentazione
	        if(!lista_slide.isEmpty())
	        {
	        	for(int i=0;i<lista_slide.size();i++)
	        	{
	        		
	        		List<String> righe_slide=lista_slide.get(i).righe_slide;
	        		lista_slide.get(i).righe_slide=new ArrayList<String>();
	        		for(int k=0;k<righe_slide.size();k++)
	        		{
	        			String[] parole_riga=righe_slide.get(k).split(" ");
	        			List<String> lista_parole_riga=new ArrayList(Arrays.asList(parole_riga));
	        			String nuova_riga="";
	        			for(int m=0;m<lista_parole_riga.size();m++)
	        			{
	        				if(nuova_riga.length()<70)
	        				{
	        					if(nuova_riga.length()==0)
	        						nuova_riga=nuova_riga+lista_parole_riga.get(m);
	        					else
	        						nuova_riga=nuova_riga+" "+lista_parole_riga.get(m);
	        					lista_parole_riga.remove(m);
	        					m--;
	        				}
	        				else
	        				{
	        					lista_slide.get(i).righe_slide.add(nuova_riga);
	        					nuova_riga="";
	        					m--;
	        				}
	        			}
	        			if(nuova_riga.length()>0)
	        				lista_slide.get(i).righe_slide.add(nuova_riga);
	        			//System.out.println(nuova_riga);
	        		}
	        		
	        		if(lista_slide.get(i).righe_slide.size()>20)
	        		{
	        			Slide slide=lista_slide.get(i);
	        			List<String> righe_slide_rimanenti=new ArrayList<String>();
	        			for(int j=20;j<slide.righe_slide.size();j++)
	        			{
	        				righe_slide_rimanenti.add(slide.righe_slide.get(j));
	        				slide.righe_slide.remove(j);
	        				j--;
	        			}
	        			Slide nuova_slide=new Slide();
	        			nuova_slide.titolo=slide.titolo;
	        			nuova_slide.righe_slide=righe_slide_rimanenti;
	        			lista_slide.add(i+1,nuova_slide);
	        		}
	        	}
	        	
	        	
	        	ppt = new XMLSlideShow();
	        	for(int i=0;i<lista_slide.size();i++)
	        	{
	        		Slide slide=lista_slide.get(i);
	        		String titolo=slide.titolo;
	        		String contenuto="-------------------------------------------------------------\n";
	        		for(int j=0;j<slide.righe_slide.size();j++)
	        		{
	        			contenuto=contenuto+slide.righe_slide.get(j)+"\n";
	        		}
	        		addSlide(ppt, titolo, contenuto);
	        	}
	        }
	        
	        fc.setCurrentDirectory(new File(fc.getSelectedFile().getAbsolutePath()));
	        fc.setSelectedFile(new File("presentazione_"+System.currentTimeMillis()));
	        //fc.getSelectedFile().renameTo(new File("presentazione_"+System.currentTimeMillis()));
	        int retrival = fc.showSaveDialog(null);
	        if (retrival == JFileChooser.APPROVE_OPTION)
	        {
	            try 
	            {
	            	try (FileOutputStream out = new FileOutputStream(fc.getSelectedFile()+".pptx")) 
	            	{
	    	            ppt.write(out);
	    	            out.flush();
	    	            //openPdfWithParams(new File(fc.getSelectedFile()+".pptx"),"");
	    	            if (Desktop.isDesktopSupported()) {
	    	                try {
	    	                    File myFile = new File(fc.getSelectedFile()+".pptx");
	    	                    Desktop.getDesktop().open(myFile);
	    	                } catch (IOException ex) {
	    	                    // no application registered for PDFs
	    	                }
	    	            }
	    	        } 
	            	catch (IOException e) {
	    	            e.printStackTrace();
	    	        }
	               //// FileWriter fw = new FileWriter(chooser.getSelectedFile()+".pptx");
	                //fw.write(sb.toString());
	            } catch (Exception ex) {
	                ex.printStackTrace();
	            }
	        }
	        
	       /* System.out.println(fc.getSelectedFile().getAbsolutePath());
	        // Salviamo la presentazione
	        try (FileOutputStream out = new FileOutputStream("HTML_Tags_Presentation16.pptx")) {
	            ppt.write(out);
	        } catch (IOException e) {
	            e.printStackTrace();
	        }*/
        }
        else
        	System.out.println("Nessun file scelto");
    }

    private static void addSlide(XMLSlideShow ppt, String title, String content) {
        XSLFSlide slide = ppt.createSlide();
        slide.getBackground().setFillColor(Color.LIGHT_GRAY);
        XSLFTextBox titleBox = slide.createTextBox();
        titleBox.setAnchor(new java.awt.Rectangle(50, 10, 600, 30));
        XSLFTextRun titleRun = titleBox.addNewTextParagraph().addNewTextRun();
        titleRun.setText(title);
        titleRun.setBold(true);
        titleRun.setFontSize(24.);

        XSLFTextBox contentBox = slide.createTextBox();
        contentBox.setAnchor(new java.awt.Rectangle(50, 40, 600, 300));
        XSLFTextParagraph contentParagraph = contentBox.addNewTextParagraph();
        XSLFTextRun contentRun = contentParagraph.addNewTextRun();
        contentRun.setText(content);
        contentRun.setFontSize(18.);
    }
    
}

