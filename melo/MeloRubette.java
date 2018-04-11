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
    //manager of data
    private MotifManager manager = null;
    //input
    private PowerDenotator scoreDeno;
    private Score score;
    private int notesLimit;
    private double span;

    public void setSpan(double value) {
        span = value;
        System.out.println("New Span: " + span);
    }

    public void setNotesLimit(int value) {
        notesLimit = value;
        System.out.println("New Notes' limit: " + value);
    }
	
    public MeloRubette() {
		//input 0: score (Power)
		//input 1: NotesLimit (Simple -> int)
        //input 2: Span (Simple -> double)
        this.setInCount(1);
        //output 0: score (Power)
        this.setOutCount(1);
    }

    public void run(RunInfo runInfo) {
        //notes.add(note.copy())
		//type 0: SimpleDenotator
		//type 1: Limit
		//type 2: CoLimit
		//type 3: PowerDenotator 
        //type 4: List
        if (getInput(0).getType() != 3) {
        } else {
            if (score == null) {
                scoreDeno = (PowerDenotator)getInput(0);
                score = new Score(scoreDeno);
                notesLimit = 1;
                span = 1.0;
            } else {
                //in case the input change 
                scoreDeno = (PowerDenotator)getInput(0);
                score = new Score(scoreDeno);
            }
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
            this.properties = new MeloDisplay(this);
        }
        return this.properties;
    }

    public void updateOutput() {
       manager = new MotifManager(scoreDeno, notesLimit, span); 
       PowerDenotator p = (PowerDenotator)manager.toDenotator();
       manager.print();
       setOutput(0, p);
    }

    public boolean applyProperties() {
        System.out.println("apply properties");
        //initialize note's limit and span
        if (scoreDeno != null) {
            System.out.println("Initializing motif manager.");
            updateOutput();
        } else {
            System.out.println("scoreDeno == null");
        }
        return true;
    }
}
