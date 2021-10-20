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

public class RigidShapeManager extends ShapeManager {
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
