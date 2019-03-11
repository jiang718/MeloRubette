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

package melo;
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

class RankIterator {
    private Shape shape;
    private int rankId;
    public RankIterator(Shape shapeT) {
        shape = shapeT;
        if (shape.getIfRev() == false) {
            rankId = 0;
        } else {
            rankId = shape.getMotifLen()-1;
        }
    }
    public boolean hasNext() {
        if (shape.getIfRev() == false) {
            return rankId < shape.getMotifLen();
        }
        return rankId >= 0;
    }
    public int next() {
        try {
            if (!hasNext()) {
                throw new Exception("wrong rank iterator range!");
            } 
        } catch (Exception e) {
            e.printStackTrace();
        }
        int rank = shape.getRank(rankId);
        if (shape.getIfRev() == false) {
            rankId++;
        } else {
            rankId--; 
        }
        return rank;
    }
}

class Shape {
    /*****Format START******/
    SimpleForm pointSingleForm;
    PowerForm pointForm;
    PowerForm pointListForm;

    SimpleForm rankForm;
    PowerForm rankListForm;

    SimpleForm weightForm;

    LimitForm shapeForm;
    /*****Format END******/

    double[][] pointList;
    int pointDimention;

    int[] rankList;
    boolean ifRev;

    double[] presenceFtList;
    double[] contentFtList;
    int diffMax;

    //getDis
    boolean ifMinimizeDis;

    //calWeight/getWeight
    double weight;
    boolean hasCalWeight;



    public Shape(double[][] pointListT, int pointDimentionT, int[] rankListT, boolean ifRevT, int diffMaxT, boolean ifMinimizeDisT) {
        genForm();
        pointList = pointListT;
        pointDimention = pointDimentionT;

        rankList = rankListT;
        ifRev = ifRevT;

        diffMax = diffMaxT;
        contentFtList = new double[diffMaxT];
        presenceFtList = new double[diffMaxT];

        ifMinimizeDis = ifMinimizeDisT; 

        weight = 0;
        hasCalWeight = false;
    }

    /***************************Format START**************************/
    //form: pointList[][] rankList[] weight
    public LimitForm getForm() {
        return shapeForm;
    }
    public void genForm() {
        //point > pointSingle
        pointSingleForm = (SimpleForm)Repository.systemRepository().getForm("Real");
        pointForm = FormFactory.makePowerForm("pointForm", pointSingleForm);
        pointListForm = FormFactory.makePowerForm("pointListForm", pointForm);

        rankForm = (SimpleForm)Repository.systemRepository().getForm("Integer");
        rankListForm = FormFactory.makePowerForm("rankListForm", rankForm);

        weightForm = (SimpleForm)Repository.systemRepository().getForm("Real");
        
        List<Form> shapeFormList = new LinkedList<Form>();
        List<String> shapeFormLabelList = new LinkedList<String>();
        shapeFormList.add(pointListForm);
        shapeFormList.add(rankListForm);
        shapeFormList.add(weightForm);
        shapeFormLabelList.add("pointList");
        shapeFormLabelList.add("rankList");
        shapeFormLabelList.add("weight");
        shapeForm = FormFactory.makeLimitForm("shapeForm", shapeFormList);
        shapeForm.setLabels(shapeFormLabelList);
    }


    public Denotator toDeno() {
        try {
            List<Denotator> pointListDenoList = new ArrayList<Denotator>();
            for (int pointId = 0; pointId < pointList.length; pointId++) {
                double[] point = pointList[pointId];
                List<Denotator> pointDenoList = new ArrayList<Denotator>();
                for (int pointSingleId = 0; pointSingleId < point.length; pointSingleId++) {
                    double pointSingle = point[pointSingleId];
                    SimpleDenotator pointSingleDeno = new SimpleDenotator(NameDenotator.make("pointSingleDeno"), pointSingleForm, new RElement(pointSingle)); 
                    pointDenoList.add(pointSingleDeno);
                }
                PowerDenotator pointDeno = new PowerDenotator(NameDenotator.make("pointDeno"), pointForm, pointDenoList);
            }
            PowerDenotator pointListDeno = new PowerDenotator(NameDenotator.make("pointListDeno"), pointListForm, pointListDenoList);
            List<Denotator> rankListDenoList = new ArrayList<Denotator>();
            for (int rankId = 0; rankId < rankList.length; rankId++) {
                int rank = rankList[rankId];
                SimpleDenotator rankDeno = new SimpleDenotator(NameDenotator.make("rankDeno"),rankForm, new ZElement(rank)); 
                rankListDenoList.add(rankDeno);
            }
            PowerDenotator rankListDeno = new PowerDenotator(NameDenotator.make("rankListDeno"), rankListForm, rankListDenoList);

            SimpleDenotator weightDeno = new SimpleDenotator(NameDenotator.make("weightDeno"),weightForm, new RElement(weight)); 

            List<Denotator> shapeDenoList = new ArrayList<Denotator>();
            shapeDenoList.add(pointListDeno);
            shapeDenoList.add(rankListDeno);
            shapeDenoList.add(weightDeno);
            LimitDenotator shapeDeno = new LimitDenotator(NameDenotator.make("shapeDeno"), shapeForm, shapeDenoList);
            return shapeDeno;
        } catch (RubatoException e) {
            e.printStackTrace();
        }
        return null;
    }
    /***************************Format END******************************/

    public void print() {
        for (int pointId = 0; pointId < size(); pointId++) {
            System.out.print("(");
            double[] point = pointList[pointId];
            for (int pointSingleId = 0; pointSingleId < pointDimention; pointSingleId++) {
                System.out.print(point[pointSingleId]);
                if (pointSingleId < pointDimention - 1) {
                    System.out.print(",");
                }
            }
            System.out.print(") ");
        }
        System.out.println();
    }

    /************Property START*****************/
    public double[] getPoint(int index) {
        return pointList[index];
    }
    public int size() {
        return pointList.length;
    }
    public int getPointDimention() {
        return pointDimention;
    }

    void setPresenceFt(int index, double presenceFt) {
        presenceFtList[index] = presenceFt;
    }
    double getPresenceFt(int index) {
        return presenceFtList[index];
    }
    void setContentFt(int index, double contentFt) {
        contentFtList[index] = contentFt;
    }
    double getContentFt(int index) {
        return contentFtList[index];
    }

    public int getRank(int index) {
        try {
            if (index < 0 || index >= rankList.length) {
                throw new Exception("wrong index in shape.");
            }
        } catch (Exception e) {}
        return rankList[index];
    }
    public RankIterator getRankIterator() {
        return new RankIterator(this);
    }
    public int getMotifLen() {
        return rankList.length;
    }
    public boolean getIfRev() {
        return ifRev;
    }
    /************Property END*******************/

    /****************Relation START******************/
    public double getDis(Shape other) {
        if (size() != other.size()) {
            System.out.println("Shape size doesn't match! (" + size() + ", " + other.size());
            System.out.println("this:");
            print();
            System.out.println("other:");
            other.print();
            return -1;
        }
        if (size() == 0) {
            return 0;
        }
        double[] dimentionShift = new double[pointDimention];
        if (ifMinimizeDis) {
            for (int pointId = 0; pointId < size(); pointId++) {
                double[] pointIn = getPoint(pointId), pointO = other.getPoint(pointId);
                for (int dimentionId = 0; dimentionId < pointIn.length; dimentionId++) {
                    dimentionShift[dimentionId] += pointIn[dimentionId] - pointO[dimentionId];
                    //if (dimentionId == 0) {
                    //    System.out.println("pitch: " + pointIn[0] + " " + pointO[0]);
                    //} else {
                    //    System.out.println("onset: " + pointIn[0] + " " + pointO[0]);
                    //}
                }
            }
            for (int dimentionId = 0; dimentionId < pointDimention; dimentionId++) {
                dimentionShift[dimentionId] = -dimentionShift[dimentionId] / size();
            }
        } 
        double sum = 0;
        for (int pointId = 0; pointId < size(); pointId++) {
            double[] pointIn = getPoint(pointId), pointO = other.getPoint(pointId);
            for (int dimentionId = 0; dimentionId < pointIn.length; dimentionId++) {
                double disFt = pointIn[dimentionId] - pointO[dimentionId] + dimentionShift[dimentionId];
                sum += disFt * disFt;
            }
        }
        double sum2 = 0;
        for (int pointId = 0; pointId < size(); pointId++) {
            double[] pointIn = getPoint(pointId), pointO = other.getPoint(pointId);
            for (int dimentionId = 0; dimentionId < pointIn.length; dimentionId++) {
                double disFt = pointIn[dimentionId] - pointO[dimentionId];
                sum2 += disFt * disFt;
            }
        }
        //System.out.println("after shifting: " + sum / size());
        //System.out.println("don't shifting: " + sum2 / size());
        return sum / size();
    }

    
    //shaper : shape
    public double getDis(int[] rankList, Shape other) {
        if (size() != rankList.length || rankList.length != other.size()) {
            System.out.println("Shape size doesn't match!");
            return -1;
        }
        if (rankList.length == 0) {
            return 0;
        }
        double[] dimentionShift = new double[pointDimention];
        if (ifMinimizeDis) {
            for (int rankId = 0; rankId < rankList.length; rankId++) {
                int pointId = rankList[rankId];
                double[] pointIn = getPoint(pointId), pointO = other.getPoint(pointId);
                for (int dimentionId = 0; dimentionId < pointIn.length; dimentionId++) {
                    dimentionShift[dimentionId] += pointIn[dimentionId] - pointO[dimentionId];
                }
            }
            for (int dimentionId = 0; dimentionId < pointDimention; dimentionId++) {
                dimentionShift[dimentionId] = -dimentionShift[dimentionId] / size();
            }
        } 
        double sum = 0;
        for (int rankId = 0; rankId < rankList.length; rankId++) {
            int pointId = rankList[rankId];
            double[] pointIn = getPoint(pointId), pointO = other.getPoint(pointId);
            for (int dimentionId = 0; dimentionId < pointIn.length; dimentionId++) {
                double disFt = pointIn[dimentionId] - pointO[dimentionId] + dimentionShift[dimentionId];
                sum += disFt * disFt;
            }
        }
        //double sum2 = 0;
        //for (int rankId = 0; rankId < rankList.length; rankId++) {
        //    int pointId = rankList[rankId];
        //    double[] pointIn = getPoint(pointId), pointO = other.getPoint(pointId);
        //    for (int dimentionId = 0; dimentionId < pointIn.length; dimentionId++) {
        //        double disFt = pointIn[dimentionId] - pointO[dimentionId];
        //        sum2 += disFt * disFt;
        //    }
        //}
        //System.out.println("after shifting: " + sum / size());
        //System.out.println("don't shifting: " + sum2 / size());
        return sum / size();
    }

    //TODO
    public boolean getIfContain(Shape shape) {
        RankIterator rankerIt = getRankIterator(); 
        RankIterator rankIt = shape.getRankIterator(); 
        if (shape.size() == 0) {
            return true;
        }
        if (size() == 0) {
            return false;
        }
        int ranker = -1, rank = -1;
        //System.out.println("in if contain:");
        //print();
        //shape.print();
        for (; rankerIt.hasNext() && rankIt.hasNext(); ) {
            //System.out.println("in side rank loop");
            ranker = rankerIt.next();
            rank = rankIt.next();
            while (rankerIt.hasNext() && ranker < rank) {
                //System.out.println("rank: " + rank + " ranker : " + ranker);
                //System.out.println("rank'hasnext: " + rankIt.hasNext() + " ranker'has : " + rankerIt.hasNext());
                ranker = rankerIt.next();
            }
            if (rankerIt.hasNext() == false) break;
            //System.out.println("before final - rank: " + rank + " ranker : " + ranker);
            //System.out.println("rank'hasnext: " + rankIt.hasNext() + " ranker'has : " + rankerIt.hasNext());
            if (ranker != rank) {
                //System.out.println("ranker != rank!");
                return false;
            }
        }
        return !rankIt.hasNext();
    }

    public int getDiff(Shape shape) {
        int shaperSize = size();
        int diff = shaperSize - shape.size();
        return diff;
    }
    /****************Relation START******************/


    /****************Target START********************/
    public double calWeight() {
        double twoFt = 1;  //2*(-diff)
        double presence = 0;
        double content = 0;
        for (int diff = 0; diff < diffMax; diff++) { 
            presence += presenceFtList[diff] * twoFt;
            content += contentFtList[diff] * twoFt;
            twoFt /= 2.0; 
        }
        weight = presence * content;
        //System.out.println("Presence: " + presence + " content : " + content);
        hasCalWeight = true;
        return weight;
    }
    public double getWeight() {
        if (!hasCalWeight) {
            System.out.println("Havn't calculate the weight!");
        }
        return weight;
    }
    /****************Target END********************/


}


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

    List<WeightedOnset> toWeighedOnsetList() {
        if (weightedOnsetList == null || weightedOnsetList.isEmpty()) {
            return null;
        }
        return weightedOnsetList;
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
}

class RigidShapeManager extends ShapeManager {
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
                        double[][] pointList = new double[motif.size()][2];
                        int[] rankList = new int[motif.size()];
                        for (int i = 0; i < motif.size(); i++) {
                            double pitch = motif.getPitch(i);
                            double onset = motif.getOnset(i);
                            int rank = motif.getRank(i);
                            pointList[i][0] = pitch;
                            pointList[i][1] = onset;
                            rankList[i] = rank;
                        }
                        boolean ifMinimizeDis = true;
                        boolean ifRev = motif.getIfRev();
                        Shape shape = new Shape(pointList, 2, rankList, ifRev, motifLib.size(), ifMinimizeDis);
                        shapeShelf.add(shape);
                    }
                }
            } else {}
        }
        System.out.println("After calculating variShapeLib");
    }

    public RigidShapeManager(Score score, List<List<List<Score>>> variMotifLib, boolean[] variSelec, int shapeSelec, double neighbour) {
        super(score, variMotifLib, variSelec, shapeSelec, neighbour);
        calVariShapeLib(variMotifLib, variSelec);
        //calVariDisMinLib(variShapeLib, variSelec);
        //calDisMinLib(variDisMinLib, variSelec);
        calPresenceAndContentFt(variShapeLib, variSelec, neighbour);
        calMotifWeight(variMotifLib, variShapeLib);
        calNoteWeight(score, variShapeLib, variMotifLib);
        calWeightedOnsetList(score);
        System.out.println("after constructing rigid shape manager");
    }
}
class DiaShapeManager extends ShapeManager {
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
                        double[][] pointList = new double[motif.size()][1];
                        int[] rankList = new int[motif.size()];
                        if (motif.size() > 0) {
                            double pitchNext = motif.getPitch(0);
                            double pitchNow = -1;
                            rankList[0] = motif.getRank(0);
                            for (int i = 1; i < motif.size(); i++) {
                                pitchNow = pitchNext;
                                pitchNext = motif.getPitch(i);
                                int rank = motif.getRank(i);
                                if (pitchNext > pitchNow) {
                                    pointList[i][0] = 1;
                                } else if (pitchNext == pitchNow) {
                                    pointList[i][0] = 0;
                                } else {
                                    pointList[i][0] = -1;
                                }
                                rankList[i] = rank;
                            }
                        }
                        boolean ifMinimizeDis = false;
                        boolean ifRev = motif.getIfRev();
                        Shape shape = new Shape(pointList, 1, rankList, ifRev, motifLib.size(), ifMinimizeDis);
                        shapeShelf.add(shape);
                    }
                }
            } else {}
        }
        System.out.println("After calculating variShapeLib");
    }
    public DiaShapeManager(Score score, List<List<List<Score>>> variMotifLib, boolean[] variSelec, int shapeSelec, double neighbour) {
        super(score, variMotifLib, variSelec, shapeSelec, neighbour);
        calVariShapeLib(variMotifLib, variSelec);
        //calVariDisMinLib(variShapeLib, variSelec);
        //calDisMinLib(variDisMinLib, variSelec);
        calPresenceAndContentFt(variShapeLib, variSelec, neighbour);
        calMotifWeight(variMotifLib, variShapeLib);
        calNoteWeight(score, variShapeLib, variMotifLib);
        calWeightedOnsetList(score);
        System.out.println("after constructing dia shape manager");
    }
}
class ElasShapeManager extends ShapeManager { 
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

public class MotifManager {
    //Format
    protected SimpleForm weightForm;
    protected LimitForm noteForm;
    protected LimitForm weightedNoteForm;
    protected PowerForm weightedScoreForm;

    protected PowerForm motifForm;
    protected LimitForm weightedMotifForm; 
    protected PowerForm weightedMotifListForm;

    Score score;
    int noteLimit;
    double span;

    boolean ifInv, ifRetro, ifRetroInv;
    int shapeSelec;
    double neighbour;

    List<List<Score>> motifLib;
    List<Integer> motifLenLib;

    List<List<List<Score>>> variMotifLib;
    boolean[] variSelec;
    protected List<ShapeManager> shapeManagerList;


    /**************************BASIC START****************************/
    public MotifManager(PowerDenotator scoreDeno, int noteLimitT, double spanT) {
        this(scoreDeno, noteLimitT, spanT, Status.CopyMode.NOCOPY);
    }
    public MotifManager(PowerDenotator scoreDeno, int noteLimitT, double spanT, Status.CopyMode mode) {
        System.out.println("Before genForm");
        //scoreOrMotifForm is needed for genForm
        score = new Score(scoreDeno, mode);
        genForm();
        System.out.println("After genForm");

        noteLimit = noteLimitT;
        System.out.println("After noteLimit");
        span = spanT;
        System.out.println("After span");
        motifLib = new ArrayList<List<Score>>();
        System.out.println("After motifLib");
        motifLenLib = new ArrayList<Integer>();
        System.out.println("After motifLenLib");
        System.out.println("Before calMotifLib");
        calMotifLib(score, noteLimit, span);
        System.out.println("After calMotifLib");
    }
    /**************************BASIC START****************************/

    /********************* Format START ***********************************/
    public void genForm() {
        //for weighted motif 
        motifForm = (PowerForm)Repository.systemRepository().getForm("Score");
        weightForm = (SimpleForm)Repository.systemRepository().getForm("Real");
        List<Form> weightedMotifFormList = new LinkedList<Form>();
        List<String> weightedMotifFormLabelList = new LinkedList<String>();
        weightedMotifFormList.add(motifForm);
        weightedMotifFormList.add(weightForm);
        weightedMotifFormLabelList.add("motif");
        weightedMotifFormLabelList.add("weight");
        weightedMotifForm = FormFactory.makeLimitForm("weightedMotif", weightedMotifFormList); 
        weightedMotifForm.setLabels(weightedMotifFormLabelList);
        weightedMotifListForm = FormFactory.makePowerForm("weightedMotifList", weightedMotifForm);

        //for weighted score 
        noteForm = (LimitForm)Repository.systemRepository().getForm("Note");
        List<Form> weightedNoteFormList = new LinkedList<Form>();
        List<String> weightedNoteFormLabelList = new LinkedList<String>();
        weightedNoteFormList.add(noteForm);
        weightedNoteFormList.add(weightForm);
        weightedNoteFormLabelList.add("Note");
        weightedNoteFormLabelList.add("Weight");
        weightedNoteForm = FormFactory.makeLimitForm("weightedNoteForm", weightedNoteFormList);
        weightedNoteForm.setLabels(weightedNoteFormLabelList);
        weightedScoreForm = FormFactory.makePowerForm("weightedScoreForm", weightedNoteForm);
    }

    List<WeightedOnset> toWeighedOnsetList() {
        if (shapeManagerList == null || shapeManagerList.get(shapeSelec) == null) {
            return null;
        }
        return shapeManagerList.get(shapeSelec).toWeightedOnsetList();
    }
    public Denotator toWeightedOnsetListDeno() {
        if (shapeManagerList == null || shapeManagerList.get(shapeSelec) == null) {
            return null;
        }
        return shapeManagerList.get(shapeSelec).toWeightedOnsetListDeno();
    }

    public Denotator toWeightedMotifListDeno() {
        try {
            List<Denotator> weightedMotifListDenoList = new ArrayList<Denotator>();
            for (int shelfId = 0; shelfId < motifLib.size(); shelfId++) {
                List<Score> motifShelf = motifLib.get(shelfId);
                for (int motifId = 0; motifId < motifShelf.size(); motifId++) {
                    List<Denotator> weightedMotifDenoList = new ArrayList<Denotator>();
                    Score motif = motifShelf.get(motifId);
                    Denotator motifDeno = motif.toScoreDeno();
                    double weight = motif.getTotalWeight();
                    Denotator weightDeno = new SimpleDenotator(NameDenotator.make("weightDeno"), weightForm, new RElement(weight));
                    weightedMotifDenoList.add(motifDeno);
                    weightedMotifDenoList.add(weightDeno);
                    LimitDenotator weightedMotifDeno = new LimitDenotator(NameDenotator.make("weightedMotifDeno"), weightedMotifForm, weightedMotifDenoList);
                    weightedMotifListDenoList.add(weightedMotifDeno);
                }
            }
            PowerDenotator weightedMotifListDeno = new PowerDenotator(NameDenotator.make("weightedMotifListDeno"), weightedMotifListForm, weightedMotifListDenoList);
            return weightedMotifListDeno;
        } catch (RubatoException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Denotator toWeightedScoreDeno() {
        try {
            List<Denotator> weightedScoreDenoList = new ArrayList<Denotator>();
            for (int noteId = 0; noteId < score.size(); noteId++) {
                Denotator noteDeno = score.getNoteDeno(noteId);
                double weight = score.getWeight(noteId);
                Denotator weightDeno = new SimpleDenotator(NameDenotator.make("weightDeno"), weightForm, new RElement(weight));
                List<Denotator> weightedNoteDenoList = new ArrayList<Denotator>();
                weightedNoteDenoList.add(noteDeno);
                weightedNoteDenoList.add(weightDeno);
                LimitDenotator weightedNoteDeno = new LimitDenotator(NameDenotator.make("weightedNote"), weightedNoteForm, weightedNoteDenoList);
                weightedScoreDenoList.add(weightedNoteDeno);
            }
            PowerDenotator weightedScoreDeno = new PowerDenotator(NameDenotator.make("weighedScoreDeno"), weightedScoreForm, weightedScoreDenoList);
            return weightedScoreDeno;
        } catch (RubatoException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Denotator toBoiledDownWeightListDeno() {
        return null;
    }
    /********************* Format END ***********************************/
    
    /**************************CalMotifLib START**********************/
    void searchMotif(List<Integer> rankList, int currentIndex, int nextAvaliableId, int noteLimit, int shelfIndex, double span) {
        if (currentIndex == noteLimit) {
            Score motif = new Score(score, new ArrayList<Integer>(rankList)); 
            motifLib.get(shelfIndex).add(motif);
            return;
        }
        for (int i = nextAvaliableId; i<=score.size()-(noteLimit-currentIndex); i++) {  
            if (currentIndex!=0) {
                //skip if two notes has same onset
                double currentOnset = score.getOnset(i);
                double previousOnset = score.getOnset(rankList.get(currentIndex-1));
                if (currentOnset-previousOnset <=1e-8) {
                    continue;
                }

                //check span
                int smallestFinalIndex=i+noteLimit-currentIndex-1;
                double finalOnset=score.getOnset(smallestFinalIndex);
                int firstIndex = rankList.get(0);
                double firstOnset = score.getOnset(firstIndex); 
                if (finalOnset-firstOnset>span) break;
            }
            rankList.add(i);
            searchMotif(rankList, currentIndex+1, i+1, noteLimit, shelfIndex, span);
            rankList.remove(currentIndex);
        }
    }
    void calMotifLib(Score score, int noteLimit, double span) {
        System.out.println("into generate motives:");
        List<Integer> rankList = new ArrayList<Integer>();
        System.out.println("notes limit: " + noteLimit);
        System.out.println("span: " + span);
        for (int i = 1; i <= noteLimit; i++) {
            motifLib.add(new ArrayList<Score>());
            motifLenLib.add(i);
            System.out.println("before search:" + i);
            int currentIndex = 0;
            int nextAvaliableId = 0;
            int limit = i;
            int shelfIndex = i-1;
            searchMotif(rankList, currentIndex, nextAvaliableId, limit, shelfIndex, span); 
            System.out.println("after search:" + i);
        }
        System.out.println("after search");
    }
    /**************************CalMotifLib END************************/

    /****************************CalWeight START*************************/
    //call by user UI Action  -> calWeight
    public void calVariSelec(boolean ifInv, boolean ifRetro, boolean ifRetroInv){ 
        System.out.println("calculating vari selec");
        System.out.println("ifInv: " + ifInv);
        System.out.println("ifRetro: " + ifRetro);
        System.out.println("ifRetroInv: " + ifRetroInv);
        variSelec = new boolean[4];
        for (int i = 0; i < 4; i++) {
            variSelec[i] = false;
        }
        variSelec[0] = true;
        int total = 0;
        if (ifInv) total++;
        if (ifRetro) total++;
        if (ifRetroInv) total++;
        if (total >= 2) {
            for (int i = 0; i < 4; i++) {
                variSelec[i] = true;
            }
        } else if (total == 1) {
            if (ifInv) {
                variSelec[1] = true;
            } else if (ifRetro) {
                variSelec[2] = true;
            } else if (ifRetroInv) {
                variSelec[3] = true;
            }
        }
        System.out.println("after calculating vari selec: " + total);
    }
    public void calVariMotifLib(List<List<Score>> motifLib, boolean[] variSelec) {
        System.out.println("calculating vari motif lib");
        List<List<Score>> invMotifLib = null, retroMotifLib = null, retroInvMotifLib = null;
        if (variSelec[1]) {
            invMotifLib = new ArrayList<List<Score>>();
        }
        if (variSelec[2]) {
            retroMotifLib = new ArrayList<List<Score>>();
        }
        if (variSelec[3]) {
            retroInvMotifLib = new ArrayList<List<Score>>();
        }
        for (int i = 0; i < motifLib.size(); i++) {
            List<Score> shelf = motifLib.get(i);
            List<Score> invShelf = new ArrayList<Score>();
            List<Score> retroShelf = new ArrayList<Score>();
            List<Score> retroInvShelf = new ArrayList<Score>();
            for (int j = 0; j < motifLib.get(i).size(); j++) {
                Score motif = shelf.get(j);
                if (variSelec[1]) {
                    Score invMotif = motif.getInv();
                    invShelf.add(invMotif);
                }
                if (variSelec[2]) {
                    Score retroMotif = motif.getRetro();
                    retroShelf.add(retroMotif);
                }
                if (variSelec[3]) {
                    Score retroInvMotif = motif.getRetroInv();
                    retroInvShelf.add(retroInvMotif);
                }
            }
            if (variSelec[1]) invMotifLib.add(invShelf);
            if (variSelec[2]) retroMotifLib.add(retroShelf);
            if (variSelec[3]) retroInvMotifLib.add(retroInvShelf);
        }
        variMotifLib = new ArrayList<List<List<Score>>>();
        variMotifLib.add(motifLib);
        variMotifLib.add(invMotifLib);
        variMotifLib.add(retroMotifLib);
        variMotifLib.add(retroInvMotifLib);
        System.out.println("after calculating variMotifLib");
    }
    public void genShapeManager(Score score, List<List<List<Score>>> variMotifLib, boolean[] variSelec, int shapeSelec, double neighbour) {
        System.out.println("gen shapeManager");
        if (shapeManagerList == null || shapeManagerList.isEmpty()) {
            shapeManagerList = new ArrayList<ShapeManager>();
            for (int i = 0; i < 3; i++) {
                shapeManagerList.add(null);
            }
        }
        //TODO
        switch (shapeSelec) {
            case 0:
                shapeManagerList.set(shapeSelec, new RigidShapeManager(score, variMotifLib, variSelec, shapeSelec, neighbour));
                break;
            case 1:
                shapeManagerList.set(shapeSelec, new DiaShapeManager(score, variMotifLib, variSelec, shapeSelec, neighbour));
                break;
            case 2:
                shapeManagerList.set(shapeSelec, new ElasShapeManager(score, variMotifLib, variSelec, shapeSelec, neighbour));
                break;
        }
        System.out.println("after gen shapeManager");
    }

    public void calWeight(boolean ifInvT, boolean ifRetroT, boolean ifRetroInvT, int shapeSelecT, double neighbourT) {
        ifInv = ifInvT;
        ifRetro = ifRetroT;
        ifRetroInv = ifRetroInvT;
        shapeSelec = shapeSelecT;
        neighbour = neighbourT;

        calVariSelec(ifInv, ifRetro, ifRetroInv);
        calVariMotifLib(motifLib, variSelec);
        genShapeManager(score, variMotifLib, variSelec, shapeSelec, neighbour);
        //test();
        //debugging
    }
    /****************************CalWeight END*************************/
    /****************************Reset Weight START***********************/

    void resetVariAndShape(boolean ifInv, boolean ifRetro, boolean ifRetroInv, int shapeSelec) {
        calWeight(ifInv, ifRetro, ifRetroInv, shapeSelec, neighbour);
    }

    void resetNeighbour(double neighbourT) {
        neighbourT = neighbour;
        genShapeManager(score, variMotifLib, variSelec, shapeSelec, neighbour);
    }

    void test() {
        //getIfContain Test
        System.out.println("test getIfContain");
        double[][] pointList1 = {{1, 0}, {2, 0}, {3, 0}};
        int[] rankList1 =  {0, 3, 4};
        boolean ifRev1 = false;
        int diffMax1= 3;
        double[][] pointList2 = {{2, 0}, {1, 0}};
        int[] rankList2 = {4, 3};
        boolean ifRev2 = true;
        int diffMax2 = 3;
        Shape s1 = new Shape(pointList1, 2, rankList1, ifRev1, diffMax1, true);
        Shape s2 = new Shape(pointList2, 2, rankList2, ifRev2, diffMax2, true);
        boolean test1 = s1.getIfContain(s2);
        System.out.println("if contain: " + test1);

        System.out.println("test inv/retro/retroInv");
        System.out.println("original score");
        int shelfId = 1;
        int motifId = 0;
        motifLib.get(shelfId).get(motifId).print();
        System.out.println("inverted score");
        Score invScore = motifLib.get(shelfId).get(motifId).getInv(); 
        invScore.print();
        System.out.println("retrograded score");
        Score retroScore = motifLib.get(shelfId).get(motifId).getRetro(); 
        retroScore.print();
        System.out.println("retroinverted score");
        Score retroInvScore = motifLib.get(shelfId).get(motifId).getRetroInv(); 
        retroInvScore.print();


        //System.out.println("test rigid/dia/elas shape");


        //System.out.println("test getDis rigid");


        //System.out.println("test getDis dia/elas");

        
    }

    /****************************Reset Weight END***********************/



    /********************* Debugging START ***********************************/
    public void print() {
        System.out.println("score: ");
        score.print();
        System.out.println("\nnotes' limit: " + noteLimit);
        System.out.println("motif library's' size: " + motifLib.size());
        for (int i = 0; i < motifLib.size(); i++) {
            System.out.println("Shelf " + (i+1) + " (Length = " + motifLenLib.get(i) + ")");
            for (int j = 0; j < motifLib.get(i).size(); j++) {
                motifLib.get(i).get(j).print();
            }
        }
    }
    /********************* Debugging END ***********************************/
}
