package melo;
import java.util.*;


//class Note {
//    private double pitch;
//    private double onset;
//    public Note copy();
//    public Note(double pitch, double onset) { this.pitch = pitch; this.onset = onset; }
//    public Note(Note note) { pitch = note.getPitch(); onset = note.getOnset(); } 
//    public double getPitch() { return pitch; }
//    public double getOnset() { return onset; }
//    public void setPitch(double pitch) { this.pitch = pitch; }
//    public void setOnset(double pitch) { this.pitch = pitch; }
//}
//
////Score object is always sorted by 1.time order
//class Score {
//    List<Note> noteList;
//    List<Integer> idList;
//    public void sort();
//    public int size();
//    public Score copy();
//    public static Score getEmpty();
//    public Score(Score score, List<Integer> idList) {}
//    public Score(List<note> noteList, List<Integer> idList);
//    public double getPitch(int index);
//    public double getOnset(int index);
//    public isMotif() { return idList != NULL; }
//    public getId(int index) { return idList.get(index); }
//    public void print() {}
//    public Score getInv();
//    public Score getRetro();
//    public Score getRetroInv();
//}
//
//
//class Shape {
//    double[] pointList;
//    int[] idList;
//    int disType;
//    public Shape(double[] pointList, int[] idList, int disType) {
//        this.pointList = pointList;
//        this.idList = idList;
//        this.disType = disType; 
//    }
//    public int getShapeLength() {
//        return pointList.length;
//    }
//    public int getMotifLength() {
//        return idList.length;
//    }
//    public double getPoint(int index) {
//        return pointList[index];
//    }
//    public double getDis(Shape other) {
//        int n = getShapeLength();
//        double shift = 0;
//        if (disType == 1) {
//            double temp = 0;
//            for (int i = 0; i < n; i++) {
//                double xi = getData(i), yi = s.getData(i);
//                temp += xi-yi;
//            }
//            shift = -temp / n;
//        } 
//        double sum = 0;
//        for (int i = 0; i < n; i++) {
//            double xi = getData(i), yi = s.getData(i);
//            sum += (xi-yi+shift)*(xi-yi+shift);
//        }
//        return Math.sqrt(sum) / n;
//    }
//}
//
//class ShapeManager {
//    protected boolean[] variSelection;
//    protected boolean[] hasCal;
//    protected double[][][] minDis;
//    protected double[][][][] variMinDis;
//    protected List<List<List<Score>>> variMotifLib;
//    protected List<List<List<Shape>>> variShapeLib;
//    public ShapeManager(boolean[] variSelection, List<List<List<Score>>> variMotifLib) { I
//        this.variSelection = variSelection;
//        hasCalDis = new boolean[] { false, false, false, false};
//        this.variMotifLib = variMotifLib;
//        List<List<Score>> lib = variMotifLib.get(0);
//        for (int vari = 0; vari < 4; vari++) {
//            variMinDis[vari] = new double[lib.size()];
//            //how many same-len-motif groups;
//            for (int group = 0; group < lib.size(); group++) {
//                List<Score> now = lib.get(group).size();//get a same-len-motif group
//                int count = now.size();
//                variMinDis[vari][group] = new double[count][count];
//                for (int i = 0; i < count; i++) {
//                    for (int j = 0; j < count; j++) {
//                        variMinDis[group][i][j] = Double.MAX_VALUE;
//                    }
//                }
//            }
//        }
//        minDis = new double[lib.size()];
//        for (int group = 0; group < lib.size(); group++) {
//            List<Score> now = lib.get(group).size();
//            int count = now.size();
//            minDis[group] = new double[count][count];
//            for (int i = 0; i < count; i++) {
//                for (int j = 0; j < count; j++) {
//                    minDis[group][i][j] = Double.MAX_VALUE;
//                }
//            }
//        }
//        variShapeLib = new ArrayList<List<List<Shape>>>();
//        for (int i = 0; i < 4; i++) {
//            variShapeLib.add(null);
//        }
//    }
//    public boolean hasVari(int i) {
//        return variSelection[i];
//    }
//    public boolean hasShape(int i) {
//        return variShapeLib.get(i) != null;
//    }
//    public boolean hasCalDis(int i) {
//        return hasCal[i];
//    }
//}
//class RigidShapeManager extends ShapeManager {
//    void calSelfShapes(List<List<Score>> motifLibrary) {
//    }
//    public RigidShapeManager(boolean[] variSelection, List<List<List<Score>>> variMotifLib) {
//        super(containInv, containRetro, containRetroInv);
//    }
//}
//class DiaShapeManager extends ShapeManager {
//    void calSelfShapes(List<List<Score>> motifLibrary) {
//    }
//    public DiaShapeManager(boolean[] variSelection, List<List<List<Score>>> variMotifLib) {
//        super(containInv, containRetro, containRetroInv);
//    }
//}
//class ElasShapeManager extends ShapeManager { 
//    void calSelfShapes(List<List<Score>> motifLibrary) {
//    }
//    public RigidShapeManager(boolean[] variSelection, List<List<List<Score>>> variMotifLib) {
//        super(containInv, containRetro, containRetroInv);
//    }
//}
//
public class MotifManager {
	private PowerForm scoreForm;
    private PowerForm motifShelfForm;	
    private SimpleForm motifLenForm;
    private LimitForm motifShelf2Form;
    private PowerForm motifLibForm;
    private List<ShapeManager> shapeManagerList;//has 3 different shapes

    //same group has the same motif
    int notesLimit; double span;
    double neighbour;
    boolean hasInv, hasRetro, hasRetroInv;

    Score score;
    List<List<Score>> motifLib;
    List<Integer> motifLenLib;
    List<List<List<Score>>> variMotifLib;


    public MotifManager(PowerDenotator scoreDeno, int notesLimitValue, double spanValue) {
        this(scoreDeno, notesLimitValue, spanValue, Status.CopyMode.NOCOPY);
    }
    public MotifManager(PowerDenotator scoreDeno, int notesLimit, double span, Status.CopyMode mode) {
        generateForm();
        this.notesLimit = notesLimit;
        this.span = spanValue;
        motifLib = new ArrayList<List<Score>>();
        motifLen = new ArrayList<Integer>();
        score = new Score(scoreDeno, mode);
        calMotifLib(score, notesLimit, span);
    }
    void searchMotif(List<Integer> idList, int currentIndex, int nextAvaliableId, int notesLimit, int shelfIndex, double span) {
        if (currentIndex == notesLimit) {
            Score motif = new Score(score, new ArrayList<Integer>(idList)); 
            motifLib.get(shelfIndex).add(motif);
            return;
        }
        for (int i = nextAvaliableId; i<=score.size()-(notesLimit-currentIndex); i++) {  
            if (currentIndex!=0) {
                //skip if two notes has same onset
                currentOnset = score.getOnset(i);
                previousOnset = score.getOnset(idList.get(currentIndex-1));
                if (currentOnset-previousOnset <=1e-8) {
                    continue;
                }
                //check span
                int smallestFinalIndex=i+notesLimit-currentIndex-1;
                double finalOnset=score.getOnset(smallestFinalIndex);
                int firstIndex = idList.get(0);
                double firstOnset = score.getOnset(firstIndex); 
                if (finalOnset-firstOnset>span) break;
            }
            idList.add(i);
            searchMotif(idList, currentIndex+1, i+1, notesLimit, shelfIndex, span);
            idList.remove(currentIndex);
        }
    }
    void calMotifLib(Score score, int notesLimit, double span) {
        System.out.println("into generate motives:");
        List<Integer> idList = new ArrayList<Integer>();
        System.out.println("notes limit: " + notesLimit);
        System.out.println("span: " + span);
        for (int i = 1; i <= notesLimit; i++) {
            motifLib.add(new ArrayList<Score>());
            motifLenLib.add(notesLimit);
            searchMotif(idList, 0, 0, i, i-1, span); 
        }
    }

    public void print() {
        System.out.println("notes' limit: " + notesLimit);
        System.out.println("motif library's' size: " + motifLib.size());
        for (int i = 0; i < motifLib.size(); i++) {
            System.out.println("Shelf " + (i+1) + " (Length = " + motifLenLib.get(i) + ")");
            for (int j = 0; j < motifLib.get(i).size(); j++) {
                motifLib.get(i).get(j).print();
            }
        }
    }

    public void generateForm() {
        scoreForm = (PowerForm)Repository.systemRepository().getForm("Score");
        motifShelfForm = FormFactory.makePowerForm("motifShelfForm", scoreForm);
        motifLenForm = (SimpleForm)Repository.systemRepository().getForm("Integer");
        List<Form> motifShelf2FormList = new LinkedList<Form>();
        List<String> motifShelf2FormLabelList = new LinkedList<String>();
        motifShelf2FormList.add(motifLenForm);
        motifShelf2FormList.add(motifShelfForm);
        motifShelf2FormLabelList.add("motifLen");
        moitfShelf2FormLabelList.add("motifShelf");
        motifShelf2Form = FormFactory.makeLimitForm("motifShelf2Form", motifShelf2List);
        motifShelf2Form.setLabels(motifShelf2LabelList);
        motifLibForm = FormFactory.makePowerForm("motifLibForm", motifShelf2Form);
    }
    public Denotator toDenotator() {
        try {
            List<Denotator> motifLibDenoList = new ArrayList<Denotator>();
            for (int i = 0; i < motifLib.size(); i++) {
                List<Denotator> motifShelfDenoList = new ArrayList<Denotator>();
                for (int j = 0; j < motifLib.get(i).size(); j++) {
                    motifShelfDenoList.add(motifLib.get(i).get(j).toDenotator()); 
                }
                PowerDenotator motifShelfDeno = new PowerDenotator(NameDenotator.make("motifShelfDeno"), motifShelfForm, motifShelfDenoList);
                SimpleDenotator motifLenDeno = new SimpleDenotator(NameDenotator.make("motifLenDeno"), motifLenForm, new ZElement(motifLenLib.get(i)));
                List<Denotator> motifShelf2DenoList = new ArrayList<Denotator>();
                motifShelf2DenoList.add(motifLenDeno); 
                motifShelf2DenoList.add(motifShelfDeno); 
                LimitDenotator motifShelf2Deno = new LimitDenotator(NameDenotator.make("motifShelf2Deno"), motifShelf2Form, motifShelf2DenoList); 
                motifLibDenoList.add(motifShelf2Deno);
            }
            Denotator d = new PowerDenotator(NameDenotator.make("motifLibDeno"), motifLibForm, motifLibDenoList);
            return d;
        } catch (RubatoException e) {
            e.printStackTrace();
        }
        return null;
    }
    ////assume the score doesn't change
    //public void calWeight(int shapeSelection, boolean containInv, boolean containRetro, boolean containRetroInv, int maxDisSimilar) {
    //    if (shapeManagers.get(shapeSelection) == null) { 
    //        switch (shapeSelection) {
    //            case 0:
    //                shapeManagers.get(shapeSelection) = new RigidShapeManager(containInv, containRetro, containRetroInv, motifLibrary);
    //                break;
    //            case 1:
    //                shapeManagers.get(shapeSelection) = new DiaShapeManager(containInv, containRetro, containRetroInv, motifLibrary);
    //                break;
    //            case 2:
    //                shapeManagers.get(shapeSelection) = new ElasticShapeManager(containInv, containRetro, containRetroInv, motifLibrary);
    //                break;
    //            default:
    //                break;
    //        }
    //    }
    //}


    //public addShape(boolean containInv, boolean containRetro, boolean containRetroInv, int shapeSelection, int maxDisSimilar) {
    //    if (shapeManager == null) {
    //        List<Shape> selfShape = calShapeList(shapeSelection, 0);
    //        shapeManager = new ShapeManager(containInv, containRetro, containRetroInv, selfShape);
    //    }
    //}
}
