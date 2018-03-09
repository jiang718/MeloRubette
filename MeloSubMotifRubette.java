/*
 * Copyright (C) 2012 Florian Thalmann
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of version 2 of the GNU General Public
 * License as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with this program; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330,
 * Boston, MA 02111-1307, USA.
 *
 */

import java.util.*;

import org.rubato.base.*;
import org.rubato.math.yoneda.*;
import org.rubato.composer.RunInfo;
import org.rubato.math.yoneda.NameDenotator;
import org.rubato.math.module.QElement;
import org.rubato.math.module.ZElement;
import org.rubato.math.module.RElement;
import org.rubato.math.arith.Rational;
//import org.rubato.rubettes.util.NoteGenerator;
import org.rubato.math.yoneda.SimpleDenotator;
import org.rubato.math.yoneda.LimitDenotator;
import org.rubato.math.module.DomainException;
import org.rubato.base.RubatoException;
import org.rubato.logeo.FormFactory;


/**
 * The Inversion Rubette stores its input denotator
 * and an inversion note denotator
 * and provides the inverted score on its outputs
 * inversion is at a (double) pitch from a pitch denotator at second input
 * @author Guerino Mazzola
 */





public class MeloSubMotifRubette extends SimpleAbstractRubette {
    protected NameDenotator emptyName = NameDenotator.make("");

    protected PowerForm scoreForm;
    protected PowerForm sameLengthMotivesForm;
    protected SimpleForm lengthForm;
    protected LimitForm sameLengthLimitForm; 
    protected PowerForm allMotivesForm;
	
	/**
	 * Creates a basic Inversion3Rubette.
	 */
    public MeloSubMotifRubette() {
		//input 0: score (Power)
		//input 1: NotesLimit (Simple -> int)
        //input 2: Span (Simple -> double)
        this.setInCount(3);
        //output 0: score (Power)
        this.setOutCount(1);
    }



    public void run(RunInfo runInfo) {
        Denotator d;
		//type 0: SimpleDenotator
		//type 1: 
		//type 2: 
		//type 3: PowerDenotator 
        if (getInput(0).getType() != 3 || getInput(1).getType() != 0 || getInput(2).getType() != 0) {

        } else {
            buildNecessaryForm();
            PowerDenotator score = (PowerDenotator)getInput(0);
            SimpleDenotator notesLimit = (SimpleDenotator)getInput(1);
            SimpleDenotator span = (SimpleDenotator)getInput(2);
            System.out.println("starts run melo");
            ArrayList<ArrayList<ArrayList<Integer>>> motives = generateMotives((PowerDenotator)getInput(0), (SimpleDenotator)getInput(1), (SimpleDenotator)getInput(2));
            System.out.println("count of notes: " + ((PowerDenotator)getInput(0)).getFactorCount());
            for (int i = 0; i < motives.size(); i++) {
                System.out.println("count of motives of length " + (i+1) + " : "  + motives.get(i).size());
            }
            PowerDenotator p = buildAllMotives(score, motives, notesLimit.getInteger());
            printAllMotives(p); 
            setOutput(0, p);
        }

        //if(3!=getInput(0).getType()||0!=getInput(1).getType()){
        //    d = getInput(0);}
        //else{
        //    d = invertScore((PowerDenotator)getInput(0), (SimpleDenotator)getInput(1));}
		//	//set the 0th output as  denotator d
        //    setOutput(0, d);
    }
    
    public void buildNecessaryForm() {
        //contains a powerform: score of score
        this.scoreForm = (PowerForm)Repository.systemRepository().getForm("Score");
        //the collection of motives of the same length
        this.sameLengthMotivesForm = FormFactory.makePowerForm("SameLengthMotivesListForm", scoreForm);
        this.lengthForm = (SimpleForm)Repository.systemRepository().getForm("Integer");

        List<Form> formList = new LinkedList<Form>();
        List<String> formListLabels = new LinkedList<String>();
        formList.add(lengthForm);
        formList.add(sameLengthMotivesForm);
        formListLabels.add("length");
        formListLabels.add("motives");
        this.sameLengthLimitForm = FormFactory.makeLimitForm("sameLengthMotiveCollection", formList);
        sameLengthLimitForm.setLabels(formListLabels);
        this.allMotivesForm = FormFactory.makePowerForm("allMotivesCollection", sameLengthLimitForm);
    }
    
    //score inversion method
    public PowerDenotator invertScore(PowerDenotator input, SimpleDenotator pitch){
		//
        PowerDenotator copyScore = input.copy();
        //getRational: returns the rational contained in a QElement denotator
        //doubleValue: converts the rational into a double
        double inversion = pitch.getRational().doubleValue();
        //the number of coordinates of the denotator
        int l = input.getFactorCount();
        for(int i = 0;i<l;i++){
            //get the ith note
            LimitDenotator notei = (LimitDenotator)copyScore.getFactor(i);
            //replace the note with its inversion
            notei = replace(notei,inversion);
        }
        return copyScore;
    }
    
    //inversion of single notes at double value invert
    public LimitDenotator replace(LimitDenotator deno, double invert){
        //0, 1, 2, 3, 4
        //onset, pitch, LoudNess, Duration, Voice
        SimpleDenotator pitch = (SimpleDenotator)deno.getFactor(1);
        //current pitch
        double pitchrat = pitch.getRational().doubleValue();
        //new pitch
        double pitchvalue =2*invert - pitchrat;
        //if new pitch < 0, replace it with original pitch (can't invert)
        double alternative = (pitchvalue>=0)?pitchvalue:pitchrat;
        //get the simple form form pitch simple denotator
        SimpleForm form = pitch.getSimpleForm();
        //generate a QElement with a new rational by alternative pitch
        QElement mod = new QElement(new Rational(alternative));
        try {
            SimpleDenotator P = new SimpleDenotator(this.emptyName,form,mod);
            //set the new pitch
            deno.setFactor(1,P);
        } catch (Exception e) {} return deno;
    }

    //get the onset from a score
    double getOnset(PowerDenotator score, int index) {
        LimitDenotator note = (LimitDenotator)score.getFactor(index);
        SimpleDenotator onset = (SimpleDenotator)note.getFactor(0);
        double onsetValue = onset.getReal();
        return onsetValue;
    }


    //DFS search for finding motives within [nextAvaliableIndex, rightBound)
    void searchMotives(ArrayList<Integer> selects, ArrayList<ArrayList<ArrayList<Integer>>> motives, int currentSelectPos, int nextAvaliableIndex, int notesLimit, double span, PowerDenotator score) {
        if (currentSelectPos == notesLimit) {
            //build a Power denotator 
            motives.get(notesLimit - 1).add(new ArrayList<Integer>(selects)); 
            return;
        }

        //cut the right bound of i so that there will be enough notes for later selections 
        for (int i = nextAvaliableIndex; i <= score.getFactorCount() - (notesLimit - currentSelectPos); i++) {  
            //if the starting index is selected (stored in selects[0])
            //the distance from the onset of the smallest last index to the starting index can't 
            //exceed the range of span
            if (currentSelectPos != 0) {
                int smallestLastIndex = i + notesLimit - currentSelectPos - 1;
                double onsetLast = getOnset(score, smallestLastIndex);
                int firstIndex = selects.get(0);
                double onsetFirst = getOnset(score, firstIndex); 
                if (onsetLast - onsetFirst > span) break;
            }
            selects.add(i);
            searchMotives(selects, motives, currentSelectPos + 1, i + 1, notesLimit, span, score);
            selects.remove(currentSelectPos);
        }
    }

    ////to generate an arraylist of an motives
    ////one motive is an arraylist of selects notes indexes from score
    ArrayList<ArrayList<ArrayList<Integer>>> generateMotives(PowerDenotator score, SimpleDenotator notesLimit, SimpleDenotator span) {
        System.out.println("into generate motives:");
        ArrayList<ArrayList<ArrayList<Integer>>> motives = new ArrayList<ArrayList<ArrayList<Integer>>>();
        ArrayList<Integer> selects = new ArrayList<Integer>();
        System.out.println("notes limit: " + notesLimit.getInteger());
        System.out.println("span: " + span.getReal());
        for (int i = 1; i <= notesLimit.getInteger(); i++) {
            //start searching from jth notes in score
            //the current notesLimit is i
            motives.add(new ArrayList<ArrayList<Integer>>());
            searchMotives(selects, motives, 0, 0, i, span.getReal(), score); 
        }
        return motives;
    }



    //
    PowerDenotator buildAllMotives(PowerDenotator score, ArrayList<ArrayList<ArrayList<Integer>>> motives, int notesLimit) {
        try { 
            List<Denotator> collectionList = new ArrayList<Denotator>();
            for (int i = 1; i <= notesLimit; i++) {
                LimitDenotator sameLengthLimitDenotator = buildMotivesOfSameLength(score, motives, i);
                collectionList.add(sameLengthLimitDenotator); 
            }
            return new PowerDenotator(NameDenotator.make(""), allMotivesForm, collectionList);
        } catch (RubatoException e) {
            e.printStackTrace();
            return null;
        }

    }

    void printAllMotives(PowerDenotator allMotives) {
        for (int i = 0; i < allMotives.getFactorCount(); i++) {
            LimitDenotator collection = (LimitDenotator)allMotives.getFactor(i);
            SimpleDenotator length = (SimpleDenotator)collection.getFactor(0);
            PowerDenotator sameLengthMotives = (PowerDenotator)collection.getFactor(1);
            System.out.println("count of the motives with lenth "  + length.getInteger() + " : " + sameLengthMotives.getFactorCount());
            for (int j = 0; j < sameLengthMotives.getFactorCount(); j++) {
                PowerDenotator motive = (PowerDenotator)sameLengthMotives.getFactor(j);
                for (int k = 0; k < motive.getFactorCount(); k++) {
                    LimitDenotator note = (LimitDenotator)motive.getFactor(k);
                    SimpleDenotator onset = (SimpleDenotator)note.getFactor(0);
                    SimpleDenotator pitch = (SimpleDenotator)note.getFactor(1);
                    System.out.print("(" + onset.getReal() + "," + pitch.getRational().intValue() +  ") ");
                }
                System.out.println();
            }
            System.out.println();
        }
    }


    //build a limit denotator: contain length and PowerDenotator of motives
    LimitDenotator buildMotivesOfSameLength(PowerDenotator score, ArrayList<ArrayList<ArrayList<Integer>>> motives, int notesLimit) {
        try {
            List<Denotator> coordinates = new ArrayList<Denotator>();
            List<Denotator> motiveList = new ArrayList<Denotator>();
            //ArrayList<ArrayList<Integer>> index from 0 .. size()
            for (int i = 0; i < motives.get(notesLimit - 1).size(); i++) {
                List<Denotator> notes = new ArrayList<Denotator>();
                for (int j = 0; j < motives.get(notesLimit - 1).get(i).size(); j++) {
                    //get a note from the score in index
                    LimitDenotator note = (LimitDenotator)score.getFactor(motives.get(notesLimit - 1).get(i).get(j));
                    //set the jth note in motive as note
                    notes.add(note);
                }
                PowerDenotator motive = new PowerDenotator(NameDenotator.make(""), this.scoreForm, notes);
                motiveList.add(motive);
            }
            PowerDenotator motivesOfSameLength = new PowerDenotator(NameDenotator.make(""), this.sameLengthMotivesForm, motiveList);
            SimpleDenotator length = new SimpleDenotator(NameDenotator.make("length"), this.lengthForm, new ZElement(notesLimit));
            coordinates.add(length);
            coordinates.add(motivesOfSameLength);
            return new LimitDenotator(NameDenotator.make(""), sameLengthLimitForm, coordinates);
        } catch (RubatoException e) {
            e.printStackTrace();
            return null;
        }
    }

   
    
    public String getGroup() {
        return "Score";
    }
    
    public String getName() {
        return "MeloSubMotif";
    }
	
    public String getShortDescription() {
        return "Generates a list of sub motives";
    }

    public String getLongDescription() {
        return "Generates a list of sub motives "+
        "to an output";
    }

    public String getInTip(int i) {
        if (i == 0) {
            return "Score Denotator";
        } else if (i == 1) {
            return "NotesLimit";
        } else {
            return "Span";
        }
    }

    public String getOutTip(int i) {
        return "Output denotator";
    }
	
}
