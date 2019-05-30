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
 * You should have received a copy of the GNU General Public * License along with this program; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330,
 * Boston, MA 02111-1307, USA.
 * */

package org.rubato.melo;
import java.util.*;
import java.lang.Double;
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

public class ElasShapeManager extends ShapeManager { 
    //variMotifLib -> variShapeLib
    void calVariShapeLib(List<List<List<Score>>> variMotifLib, boolean[] variSelec) {
        System.out.println("calculating variShapeLib");
        variShapeLib = new ArrayList<List<List<Shape>>>();
        for (int variId = 0; variId < 4; variId++) {
            if (variSelec[variId] == true) {
                List<List<Score>> motifLib = variMotifLib.get(variId);
                variShapeLib.add(new ArrayList<List<Shape>>());
                List<List<Shape>> shapeLib = variShapeLib.get(variId);
                for (int shelfId = 0;  shelfId < motifLib.size(); shelfId++) {
                    List<Score> motifShelf = motifLib.get(shelfId);
                    shapeLib.add(new ArrayList<Shape>());
                    List<Shape> shapeShelf = shapeLib.get(shelfId);
                    for (int motifId = 0; motifId < motifShelf.size(); motifId++) {
                        Score motif = motifShelf.get(motifId);
                        try {
                            if (motif.size() == 0) {
                                throw new Exception("motif size == 0 error!");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        int[] rankList = new int[motif.size()];
                        double[][] pointList = new double[(motif.size()-1)*2][1];
                        for (int noteId = 0; noteId < motif.size(); noteId++) {
                            rankList[noteId] = motif.getRank(noteId);
                        }
                        double pitchNext = motif.getPitch(0);
                        double onsetNext = motif.getOnset(0);
                        double pitchNow = -1;
                        double onsetNow = -1;
                        //from [0, motif.size()-1] li/ltotal
                        //from [motif.size()-1, (motif.size()-1)*2-1] : tangent(angle)
                        double disSum = 0;
                        for (int noteId = 1; noteId < motif.size(); noteId++) {
                            pitchNow = pitchNext; 
                            onsetNow = onsetNext;
                            pitchNext = motif.getPitch(noteId);
                            onsetNext = motif.getOnset(noteId);
                            double pitchD = pitchNext - pitchNow;
                            double onsetD = onsetNext - onsetNow;
                            pointList[noteId-1][0]=Math.sqrt(pitchD*pitchD+onsetD * onsetD);
                            disSum += pointList[noteId-1][0];
                            if (pitchD < 0) pitchD = -pitchD;
                            pointList[noteId-1+(motif.size()-1)][0] = pitchD / onsetD;
                        }
                        for (int pointId= 0; pointId < motif.size()-1; pointId++) {
                            pointList[pointId][0] /= disSum; 
                        }
                        boolean ifMinimizeDis = false;
                        boolean ifRev = motif.getIfRev();
                        Shape shape = new Shape(pointList, 1, rankList, ifRev, (motifLib.size()-1)*2 + 1, ifMinimizeDis);
                        shapeShelf.add(shape);
                    }
                }
            } else {}
        }
        System.out.println("After calculating variShapeLib");
    }
    public ElasShapeManager(Score score, List<List<List<Score>>> variMotifLib, boolean[] variSelec, int shapeSelec, double neighbour) {
        super(score, variMotifLib, variSelec, shapeSelec, neighbour);
        calVariShapeLib(variMotifLib, variSelec);
        //calVariDisMinLib(variShapeLib, variSelec);
        //calDisMinLib(variDisMinLib, variSelec);
        calPresenceAndContentFt(variShapeLib, variSelec, neighbour);
        calMotifWeight(variMotifLib, variShapeLib);
        calNoteWeight(score, variShapeLib, variMotifLib);
        calWeightedOnsetList(score);
        System.out.println("after constructing elas shape manager");
    }
}
