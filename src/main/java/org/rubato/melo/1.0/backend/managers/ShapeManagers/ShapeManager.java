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

class ShapeManager {
    PowerForm weightedOnsetListForm;

    protected Score score;
    protected List<List<List<Score>>> variMotifLib;//0-3 variation
    protected boolean[] variSelec;
    protected int shapeSelec;
    protected double neighbour;

    //-------------------
    protected List<List<List<Shape>>> variShapeLib;
    protected double[][][][] variDisMinLib;
    protected double[][][] disMinLib;
    protected List<WeightedOnset> weightedOnsetList;
    //protected boolean[] hasCalDis;

    public ShapeManager(Score scoreT, List<List<List<Score>>> variMotifLibT, boolean[] variSelecT, int shapeSelecT, double neighbourT) {
        score = scoreT;
        variMotifLib = variMotifLibT;
        variSelec = variSelecT;
        shapeSelec = shapeSelecT;
        neighbour = neighbourT;
        genForm();
    }

    public Denotator toDeno() {
        PowerForm shapeLibForm = null;
        PowerForm shapeShelfForm = null;
        try {
            List<List<Shape>> selfShapeLib = variShapeLib.get(0);
            List<Denotator> shapeLibDenoList = new ArrayList<Denotator>();
            for (int shelfId = 0; shelfId < selfShapeLib.size(); shelfId++) {
                List<Shape> shapeShelf = selfShapeLib.get(shelfId);
                List<Denotator> shapeShelfDenoList = new ArrayList<Denotator>();
                LimitForm shapeForm = null;
                for (int shapeId = 0; shapeId < shapeShelf.size(); shapeId++) {
                    Shape shape = shapeShelf.get(shapeId);
                    Denotator shapeDeno = shape.toDeno();
                    shapeShelfDenoList.add(shapeDeno);
                    if (shapeId == 0) shapeForm = shape.getForm();
                }
                shapeShelfForm = FormFactory.makePowerForm("shapeShelfForm", shapeForm);
                PowerDenotator shapeShelfDeno = new PowerDenotator(NameDenotator.make("shapeShelfDeno"), shapeShelfForm, shapeShelfDenoList);
                shapeLibDenoList.add(shapeShelfDeno);
            }
            shapeLibForm = FormFactory.makePowerForm("shapeLibForm", shapeShelfForm);
            PowerDenotator shapeLibDeno = new PowerDenotator(NameDenotator.make("shapeLib"), shapeLibForm, shapeLibDenoList);
            return shapeLibDeno;

        } catch (RubatoException e) {}
        return null;
    }

    void calVariDisMinLib(List<List<List<Shape>>> variShapeLib, boolean[] variSelec) {
        System.out.println("calculating variDisMinLib");
        variDisMinLib = new double[4][][][];
        for (int variId = 0; variId < 4; variId++) {
            if (variSelec[variId] == true) {
                //System.out.println("vari selection: " + variId);
                List<List<Shape>> shapeLib = variShapeLib.get(variId);
                variDisMinLib[variId] = new double[shapeLib.size()][][];
                double[][][] psDisMinLib = variDisMinLib[variId];
                for (int shelfId = 0; shelfId < shapeLib.size(); shelfId++) {
                    List<Shape> shapeShelf = shapeLib.get(shelfId);
                    psDisMinLib[shelfId] = new double[shapeShelf.size()][shapeShelf.size()];
                    double[][] psDisMinShelf = psDisMinLib[shelfId];
                    for (int shapeIId = 0; shapeIId < shapeShelf.size(); shapeIId++) {
                        Shape shapeI = shapeShelf.get(shapeIId);
                        for (int shapeJId = shapeIId; shapeJId < shapeShelf.size(); shapeJId++) {
                            Shape shapeJ = shapeShelf.get(shapeJId);
                            psDisMinShelf[shapeIId][shapeJId] = shapeI.getDis(shapeJ);
                            psDisMinShelf[shapeJId][shapeIId]=psDisMinShelf[shapeIId][shapeJId];
                        }
                    }
                }
            }
        }
        System.out.println("After calculating variDisMinLib");
    }

    void calDisMinLib(double[][][][] variDisMinLib, boolean[] variSelec) {
        System.out.println("calculating disMinLib");
        double[][][] selfDisMinLib = variDisMinLib[0];
        System.out.println("selfDisMinLib's shelf count:" + selfDisMinLib.length);
        disMinLib = new double[selfDisMinLib.length][][];
        for (int shelfId = 0; shelfId < selfDisMinLib.length; shelfId++) {
            double[][] selfDisMinShelf = selfDisMinLib[shelfId];
            System.out.println("selfDisMinShelf's length count:" + selfDisMinShelf.length);
            disMinLib[shelfId] = new double[selfDisMinShelf.length][selfDisMinShelf.length];
            double[][] disMinShelf = disMinLib[shelfId];
            for (int disI = 0; disI < selfDisMinShelf.length; disI++) {
                for (int disJ = 0; disJ < selfDisMinShelf.length; disJ++) {
                    disMinShelf[disI][disJ] = selfDisMinShelf[disI][disJ];
                }
            }
        }
        System.out.println("calculating disMinLib init");
        for (int variId = 1; variId < 4; variId++) {
            if (variSelec[variId] == true) {
                double[][][] psDisMinLib = variDisMinLib[variId];
                for (int shelfId = 0; shelfId < disMinLib.length; shelfId++) {
                    double[][] psDisMinShelf = psDisMinLib[shelfId];
                    double[][] disMinShelf = disMinLib[shelfId];
                    for (int disI = 0; disI < disMinShelf.length; disI++) {
                        for (int disJ = 0; disJ < disMinShelf.length; disJ++) {
                            double psDisMin = psDisMinShelf[disI][disJ];
                            if (psDisMin < disMinShelf[disI][disJ]) {
                                disMinShelf[disI][disJ] = psDisMin;
                            }
                        }
                    }
                }
            }
        }
        System.out.println("After calculating disMinLib");
    }

    void calPresenceAndContentFt(List<List<List<Shape>>> variShapeLib, boolean[] variSelec, double neighbour) {
        System.out.println("calculating presenceFt and contentFt");
        List<List<Shape>> selfShapeLib = variShapeLib.get(0);
        for (int shelfId = 0; shelfId < selfShapeLib.size(); shelfId++) {
            //System.out.println("shelfId: " + shelfId);
            //double[][] disMinShelf = disMinLib[shelfId];
            List<Shape> shapeShelf = selfShapeLib.get(shelfId);
            for (int shapeIId= 0; shapeIId < shapeShelf.size(); shapeIId++) {
                Shape shapeI = shapeShelf.get(shapeIId);
                for (int shapeJId= shapeIId; shapeJId< shapeShelf.size(); shapeJId++) {
                    double disMinIJ = Double.MAX_VALUE;
                    //find the minimum dis between variation
                    for (int variId = 0; variId < 4; variId++) {
                        if (variSelec[variId]) {
                            Shape shapeJ = variShapeLib.get(variId).get(shelfId).get(shapeJId);
                            //System.out.println("vari " + (variId+1));
                            //shapeJ.print();
                            double disMinIJTemp = shapeI.getDis(shapeJ);
                            if (disMinIJTemp < disMinIJ) {
                                disMinIJ = disMinIJTemp;
                            }
                        }
                    }
                    Shape shapeJ = shapeShelf.get(shapeJId);

                    //similar
                    if (disMinIJ <= neighbour) {
                    //if (disMinShelf[shapeIId][shapeJId] <= neighbour) {
                        for (int shelfIder = shelfId; shelfIder < selfShapeLib.size(); shelfIder++) {
                            List<Shape> shapeShelfer = selfShapeLib.get(shelfIder);
                            for(int shapeIder=0; shapeIder<shapeShelfer.size();shapeIder++) {
                                Shape shaper = shapeShelfer.get(shapeIder);
                                //System.out.println("Shape I: " );
                                //shapeI.print();
                                //System.out.println("Shape J: " );
                                //shapeJ.print();
                                //System.out.println("Shaper: " );
                                //shaper.print();
                                int diff = shaper.getDiff(shapeI);
                                if (shaper.getIfContain(shapeI)) {
                                    //System.out.println("Shaper contains shapeI." );
                                    shapeI.setPresenceFt(diff, shapeI.getPresenceFt(diff)+1);
                                    shaper.setContentFt(diff, shaper.getContentFt(diff)+1);
                                }
                                if (shapeJId > shapeIId && shaper.getIfContain(shapeJ)) {
                                    //System.out.println("Shaper contains shapeJ." );
                                    shapeJ.setPresenceFt(diff, shapeJ.getPresenceFt(diff)+1);
                                    shaper.setContentFt(diff, shaper.getContentFt(diff)+1);
                                }
                                //System.out.println();
                            }
                        }
                    }
                }
            }
        }
        System.out.println("after calculting presence and content");
    }

    void calMotifWeight(List<List<List<Score>>> variMotifLib, List<List<List<Shape>>> variShapeLib) {
        System.out.println("calculating shape weight");
        List<List<Shape>> selfShapeLib = variShapeLib.get(0);
        List<List<Score>> selfMotifLib = variMotifLib.get(0);
        for (int shelfId = 0; shelfId < selfShapeLib.size(); shelfId++) {
            List<Shape> shapeShelf = selfShapeLib.get(shelfId);
            List<Score> motifShelf = selfMotifLib.get(shelfId);
            for (int shapeId = 0; shapeId < shapeShelf.size(); shapeId++) {
                Shape shape = shapeShelf.get(shapeId);
                Score motif = motifShelf.get(shapeId);
                double weight = shape.calWeight();
                motif.setTotalWeight(weight);
            }
        }
        System.out.println("after calculating shape weight");
    }

    void calNoteWeight(Score score, List<List<List<Shape>>> variShapeLib, List<List<List<Score>>> variMotifLib) {
        System.out.println("calculating note weight");
        List<List<Shape>> selfShapeLib = variShapeLib.get(0);
        List<List<Score>> selfMotifLib = variMotifLib.get(0);
        for (int shelfId = 0; shelfId < selfShapeLib.size(); shelfId++) {
            List<Shape> shapeShelf = selfShapeLib.get(shelfId);
            List<Score> motifShelf = selfMotifLib.get(shelfId);
            for (int shapeId = 0; shapeId < shapeShelf.size(); shapeId++) {
                Shape shape = shapeShelf.get(shapeId);
                Score motif = motifShelf.get(shapeId);
                //shape.getMotifLen = motif.size();
                for (int rankId = 0; rankId < shape.getMotifLen(); rankId++) {
                    int rank = shape.getRank(rankId);
                    score.setWeight(rank, score.getWeight(rank) + shape.getWeight());
                }
                for (int noteId = 0; noteId < motif.size(); noteId++) {
                    motif.setWeight(noteId, motif.getWeight(noteId) + shape.getWeight());
                }
            }
        }
        System.out.println("after calculating note weight");
    }

    void genForm() {
        weightedOnsetListForm = FormFactory.makePowerForm("weightedOnsetList", new WeightedOnset().getForm());
    }

    void calWeightedOnsetList(Score score) {
        //onset //weight
        weightedOnsetList = new ArrayList<WeightedOnset>();
        double onsetPrev = -1;
        for (int noteId = 0; noteId < score.size(); noteId++) {
            double onsetNow = score.getOnset(noteId);
            if (onsetNow == onsetPrev) {
                int index = weightedOnsetList.size()-1;
                WeightedOnset weightedOnset = weightedOnsetList.get(index);
                double weight = weightedOnset.getWeight() + score.getWeight(noteId);
                weightedOnset.setWeight(weight);
            } else {
                double weight = score.getWeight(noteId);
                WeightedOnset weightedOnset = new WeightedOnset(onsetNow, weight);
                weightedOnsetList.add(weightedOnset);
            }
            onsetPrev = onsetNow;
        }
    }

    Denotator toWeightedOnsetListDeno() {
        if (weightedOnsetList == null || weightedOnsetList.isEmpty()) {
            return null;
        }
        try {
            List<Denotator> weightedOnsetListDenoList = new ArrayList<Denotator>();
            for (int weightedOnsetId = 0; weightedOnsetId < weightedOnsetList.size(); weightedOnsetId++) {
                WeightedOnset weightedOnset = weightedOnsetList.get(weightedOnsetId);
                weightedOnset.print();
                List<Denotator> weightedOnsetDenoList = new ArrayList<Denotator>();
                weightedOnsetListDenoList.add(weightedOnset.toDeno());
            }
            PowerDenotator weightedOnsetListDeno = new PowerDenotator(NameDenotator.make("weightedOnsetListDeno"), weightedOnsetListForm, weightedOnsetListDenoList);
            return weightedOnsetListDeno;
        } catch (RubatoException e) {
            e.printStackTrace();
        }
        return null;
    }

    /************ Internal Data Output BEGIN ****************/
    List<WeightedOnset> getWeightedOnsetList() {
        if (weightedOnsetList == null || weightedOnsetList.isEmpty()) {
            return null;
        }
        return weightedOnsetList;
    }
    /************ Internal Data Output END ****************/
}
