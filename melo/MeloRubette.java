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


package melo;
import java.util.*; 
import javax.swing.*;
import java.awt.event.*;
import java.awt.*;

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
 * The Melo Rubette which generates and analyzes motives
 * @author Mijia Jiang 
 */

public class MeloRubette extends SimpleAbstractRubette {
    //protected NameDenotator emptyName = NameDenotator.make("");

    //protected PowerForm scoreForm;
    //protected PowerForm sameLengthMotivesForm;
    //protected SimpleForm lengthForm;
    //protected LimitForm sameLengthLimitForm; 
    //protected PowerForm allMotivesForm;

    //UI
    private JPanel properties = null;
    //motifManager of data
    private MotifManager motifManager = null;
    //input
    private PowerDenotator scoreDeno = null, scoreDenoPrev = null;
    private Score score;
    //UI Input
    private boolean ifInv = false, ifRetro = false, ifRetroInv = false;
    private int noteLimit = 1;
    private double span = 1;
    private int shapeSelec = 0;
    private double neighbour = 5;
    private boolean ifNoteLimitChanged = false, ifSpanChanged = false;
    private boolean ifVariChanged = true;
    private boolean ifShapeSelecChanged = true;
    private boolean ifNeighbourChanged = true;


    //
    public void setNoteLimit(int noteLimitT) {
        if (noteLimit != noteLimitT) {
            ifNoteLimitChanged = true;
        }
        noteLimit = noteLimitT;
        System.out.println("New Notes' limit: " + noteLimit);
    }
    public void setSpan(double spanT) {
        if (span != spanT) {
            ifSpanChanged = true;
        }
        span = spanT;
        System.out.println("New Span: " + span);
    }
    //
    public void setIfInv(boolean ifInvT){
        if (ifInv != ifInvT) {
            ifVariChanged = true;
        }
        ifInv = ifInvT;
        System.out.println("New ifInv: " + ifInv);
    }
    public void setIfRetro(boolean ifRetroT){
        if (ifRetro != ifRetroT) {
            ifVariChanged = true;
        }
        ifRetro = ifRetroT;
        System.out.println("New ifRetro: " + ifRetro);
    }
    public void setIfRetroInv(boolean ifRetroInvT){
        if (ifRetroInv != ifRetroInvT) {
            ifVariChanged = true;
        }
        ifRetroInv = ifRetroInvT;
        System.out.println("New ifRetroInv: " + ifRetroInv);
    }
    //
    public void setShapeSelec(int shapeSelecT) {
        if (shapeSelec != shapeSelecT) {
            ifShapeSelecChanged = true;
        }
        shapeSelec = shapeSelecT;
        System.out.println("New Shape Selection: " + shapeSelec);
    }
    //
    public void setNeighbour(double neighbourT) {
        if (neighbour != neighbourT) {
            ifNeighbourChanged = true;
        }
        neighbour = neighbourT;
        System.out.println("New Neighbour: " + neighbour);
    }

    public void calWeight() {
        calWeight(noteLimit, span, ifInv, ifRetro, ifRetroInv, shapeSelec, neighbour);
    }
    public void calWeight(int noteLimitT, double spanT, double neighbourT) {
        calWeight(noteLimitT, spanT, ifInv, ifRetro, ifRetroInv, shapeSelec, neighbourT);
    }


    public void calWeight(int noteLimit, double span, boolean ifInv, boolean ifRetro, boolean ifRetroInv, int shapeSelec, double neighbour) {
        long startTime = System.nanoTime();
        setNoteLimit(noteLimit);
        setSpan(span);
        setIfInv(ifInv);
        setIfRetro(ifRetro);
        setIfRetroInv(ifRetroInv);
        setShapeSelec(shapeSelec);
        setNeighbour(neighbour);

        //if
        if (score == null) {
            System.out.println("Run rubato to set score first");
        } else {
            System.out.println("Calculatint the weight...");
            motifManager = new MotifManager(scoreDeno, noteLimit, span); 
            motifManager.calWeight(ifInv, ifRetro, ifRetroInv, shapeSelec, neighbour);
            //if (ifNoteLimitChanged || ifSpanChanged) {
            //    System.out.println("Note limit or span changed...");
            //    ifNoteLimitChanged = false;
            //    ifSpanChanged = false;
            //    ifVariChanged = false;
            //    ifNeighbourChanged = false;
            //} else if (ifVariChanged || ifShapeSelecChanged) {
            //    System.out.println("vari changed...");
            //    motifManager.resetVariAndShape(ifInv, ifRetro, ifRetroInv, shapeSelec);
            //    ifVariChanged = false;
            //    ifNeighbourChanged = false;
            //} else if (ifNeighbourChanged) {
            //    System.out.println("neighbour changed...");
            //    motifManager.resetNeighbour(neighbour);
            //    ifNeighbourChanged = false;
            //} else {
            //    System.out.println("no thing changed...");
            //}
            motifManager.print();
            updateOutput();
        }
        long endTime = System.nanoTime();
        long totalTimeDuration = (endTime - startTime) / 1000; //in secs
        System.out.println("Calculation Elapsed time: "  + totalTimeDuration + " secs");
    }


	
    public MeloRubette() {
		//input 0: score (Power)
		//input 1: NoteLimit (Simple -> int)
        //input 2: Span (Simple -> double)
        this.setInCount(1);
        //output 0: score (Power)
        this.setOutCount(3);
    }

    public void run(RunInfo runInfo) {
        //notes.add(note.copy())
		//type 0: SimpleDenotator
		//type 1: Limit
		//type 2: CoLimit
		//type 3: PowerDenotator 
        //type 4: List
        if (getInput(0).getType() != 3) {
		//TODO        
	} else {
            scoreDenoPrev = scoreDeno;
            scoreDeno = (PowerDenotator)getInput(0);
            score = new Score(scoreDeno);
            //calWeight();
            updateOutput();
        }
    }
   
    
    public String getGroup() {
        return "Score";
    }
    
    public String getName() {
        return "Melo";
    }
	
    public String getShortDescription() {
        return "Generates a list of sub motives";
    }

    public String getLongDescription() {
        return "Generates a list of sub motives "+
        "to an output";
    }

    public String getInTip(int i) {
        return "Score Denotator";
    }

    public String getOutTip(int i) {
        return "Output denotator";
    }

    public boolean hasProperties() {
        return true;
    }
    public JComponent getProperties() {
        if (this.properties == null) {
            this.properties = new MeloMainScreen(this);
        }
        return this.properties;
    }

    public void updateOutput() {
        if ((scoreDenoPrev != null && !scoreDenoPrev.equals(scoreDeno)) || motifManager == null) {
            motifManager = new MotifManager(scoreDeno, noteLimit, span); 
            motifManager.print();
        }

        Denotator weightedMotifListDeno  = motifManager.toWeightedMotifListDeno();
        Denotator weightedScoreDeno  = motifManager.toWeightedScoreDeno();
        Denotator weightedOnsetListDeno = motifManager.toWeightedOnsetListDeno();
        //System.out.println("Set output ( weighted motif list deno, etc..)");
        setOutput(0, weightedMotifListDeno);
        PowerDenotator weightedMotifListDeno2 = (PowerDenotator)weightedMotifListDeno;
        LimitDenotator weightedMotifDeno = (LimitDenotator)weightedMotifListDeno2.getFactor(0);
        SimpleDenotator weightDeno = (SimpleDenotator)weightedMotifDeno.getFactor(1);
        //System.out.println("weightedMotif first weight: " + weightDeno.getReal());
        setOutput(1, weightedScoreDeno);
        setOutput(2, weightedOnsetListDeno);
    }


    public boolean applyProperties() {
        MeloMainScreen mainScreen = (MeloMainScreen) properties;
        if (mainScreen != null) {
            System.out.println("notelimit from screen: " + mainScreen.getNoteLimit());
            calWeight(mainScreen.getNoteLimit(), mainScreen.getSpan(), mainScreen.getNeighbour());
        }
        //TODO
        //System.out.println("apply properties");
        ////initialize note's limit and span
        //if (scoreDeno != null) {
        //    System.out.println("Initializing motif motifManager.");
        //    updateOutput();
        //} else {
        //    System.out.println("scoreDeno == null");
        //}
        return true;
    }

}
