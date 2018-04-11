/*
 * Copyright (C) 2012 Florian Thalmann *
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
 * */

package melo;

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
 * @author Mijia Jiang 
 */

public class MotifManager {
	private PowerForm scoreForm;
    private PowerForm sameLengthMotivesForm;	
    private SimpleForm lengthForm;
    private LimitForm sameLengthLimitForm;
    private PowerForm allMotivesForm;

    //same group has the same motif
    int notesLimit;
    double span;
    ArrayList<ArrayList<Score>> motifGroups;
    Score score;
    ArrayList<Integer> motifGroupSizes;
       
    
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

    public MotifManager(PowerDenotator scoreDeno, int notesLimitValue, double spanValue) {
        this(scoreDeno, notesLimitValue, spanValue, Status.CopyMode.NOCOPY);
    }
    public MotifManager(PowerDenotator scoreDeno, int notesLimitValue, double spanValue, Status.CopyMode mode) {
        buildNecessaryForm();
        notesLimit = notesLimitValue;
        span = spanValue;
        motifGroups = new ArrayList<ArrayList<Score>>();
        motifGroupSizes = new ArrayList<Integer>();
        score = new Score(scoreDeno, mode);
        generateMotives(score, notesLimit, span);
    }

    //DFS search for finding motives within [nextAvaliableIndex, rightBound)
    void searchMotives(ArrayList<Integer> selects, int currentSelectPos, int nextAvaliableIndex, int notesLimit, int groupIndex, double span) {
        if (currentSelectPos == notesLimit) {
            //build a Power denotator 
            //update motif
            //System.out.println("Find a new motif for group " + (groupIndex + 1));
            Score motif = new Score(score, new ArrayList<Integer>(selects)); 
            motifGroups.get(groupIndex).add(motif);
            //motifGroups.get(groupIndex).get(motifGroups.size()-1).print();
            //motives.get(notesLimit - 1).add(new ArrayList<Integer>(selects)); 
            return;
        }

        //cut the right bound of i so that there will be enough notes for later selections 
        for (int i = nextAvaliableIndex; i <= score.size() - (notesLimit - currentSelectPos); i++) {  
            //if the starting index is selected (stored in selects[0])
            //the distance from the onset of the smallest last index to the starting index can't 
            //exceed the range of span
            if (currentSelectPos != 0) {
                int smallestLastIndex = i + notesLimit - currentSelectPos - 1;
                double onsetLast =score.getOnset(smallestLastIndex);
                int firstIndex = selects.get(0);
                double onsetFirst = score.getOnset(firstIndex); 
                if (onsetLast - onsetFirst > span) break;
            }
            selects.add(i);
            searchMotives(selects, currentSelectPos + 1, i + 1, notesLimit, groupIndex, span);
            selects.remove(currentSelectPos);
        }
    }

    //to generate an arraylist of an motives
    //one motive is an arraylist of selects notes indexes from score
    void generateMotives(Score score, int notesLimit, double span) {
        System.out.println("into generate motives:");
        ArrayList<Integer> selects = new ArrayList<Integer>();
        System.out.println("notes limit: " + notesLimit);
        System.out.println("span: " + span);
        for (int i = 1; i <= notesLimit; i++) {
            //start searching from jth notes in score
            //the current notesLimit is i
            motifGroups.add(new ArrayList<Score>());
            motifGroupSizes.add(notesLimit);
            //motives.add(new ArrayList<ArrayList<Integer>>());
            searchMotives(selects, 0, 0, i, i-1, span); 
        }
    }

    public Denotator toDenotator() {
        try {
            List<Denotator> groupList = new ArrayList<Denotator>();
            for (int i = 0; i < motifGroups.size(); i++) {
                //ArrayList<Score>(samelength)
                //
                List<Denotator> motifList = new ArrayList<Denotator>();
                for (int j = 0; j < motifGroups.get(i).size(); j++) {
                    motifList.add(motifGroups.get(i).get(j).toDenotator()); 
                }
                PowerDenotator motivesOfSameLength = new PowerDenotator(NameDenotator.make("sameLengthMotives"), sameLengthMotivesForm, motifList);
                SimpleDenotator length = new SimpleDenotator(NameDenotator.make("length"), this.lengthForm, new ZElement(motifGroupSizes.get(i)));
                List<Denotator> sameLengthCollection = new ArrayList<Denotator>();
                sameLengthCollection.add(length); 
                sameLengthCollection.add(motivesOfSameLength); 
                LimitDenotator currentGroup = new LimitDenotator(NameDenotator.make("sameLengthCollection"), sameLengthLimitForm, sameLengthCollection); 
                groupList.add(currentGroup);
            }
            Denotator d = new PowerDenotator(NameDenotator.make("allMotivesCollection"), this.allMotivesForm, groupList);
            return d;
        } catch (RubatoException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void print() {
        System.out.println("notes' limit: " + notesLimit);
        System.out.println("motifGroup's' size: " + motifGroups.size());
        for (int i = 0; i < motifGroups.size(); i++) {
            System.out.println("Group " + (i+1) + " (Length = " + motifGroupSizes.get(i) + ")");
            for (int j = 0; j < motifGroups.get(i).size(); j++) {
                motifGroups.get(i).get(j).print();
            }
        }
    }
}
