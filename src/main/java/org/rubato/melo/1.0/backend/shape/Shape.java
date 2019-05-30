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

public class Shape {
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
