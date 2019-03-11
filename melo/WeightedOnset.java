
package melo; 
import java.util.*;
import org.rubato.base.*;
import org.rubato.math.yoneda.*;
import org.rubato.composer.RunInfo;
import org.rubato.math.yoneda.NameDenotator;
import org.rubato.math.module.RElement;
import org.rubato.math.arith.Rational;
import org.rubato.math.yoneda.SimpleDenotator;
import org.rubato.math.yoneda.LimitDenotator;
import org.rubato.base.RubatoException;
import org.rubato.logeo.FormFactory;


/**
 * @author Mijia Jiang 
 * This is manager class for a single note
 */

public class WeightedOnset {
    SimpleForm onsetForm;
    SimpleForm weightForm; 
    LimitForm weightedOnsetForm;

    private double onset;
    private double weight;

    /**************************FORMAT START***************************/
    public LimitForm getForm() {
        return weightedOnsetForm;
    }
    public void genForm() {
        onsetForm = (SimpleForm)Repository.systemRepository().getForm("Onset");
        weightForm = (SimpleForm)Repository.systemRepository().getForm("Onset");
        List<Form> weightedOnsetFormList = new LinkedList<Form>();
        List<String> weightedOnsetFormLabelList = new LinkedList<String>();
        weightedOnsetFormLabelList.add("onset");
        weightedOnsetFormLabelList.add("weight");
        weightedOnsetForm = FormFactory.makeLimitForm("weighteOnset", weightedOnsetFormList);
        weightedOnsetForm.setLabels(weightedOnsetFormLabelList);
    }
    public Denotator toDeno() {
        try {
            List<Denotator> weightedOnsetDenoList = new ArrayList<Denotator>();
            SimpleDenotator onsetDeno = new SimpleDenotator(NameDenotator.make("onsetDeno"), onsetForm, new RElement(onset));
            SimpleDenotator weightDeno = new SimpleDenotator(NameDenotator.make("weightDeno"), weightForm, new RElement(weight));
            weightedOnsetDenoList.add(onsetDeno);
            weightedOnsetDenoList.add(weightDeno);
            LimitDenotator weightedOnsetDeno = new LimitDenotator(NameDenotator.make("weightedOnsetDeno"), weightedOnsetForm, weightedOnsetDenoList);
            return weightedOnsetDeno;
        } catch (RubatoException e) {
            e.printStackTrace();
        }
        return null;
    }
    /**************************FORMAT END*****************************/

    /**************************BASIC START****************************/
    public WeightedOnset(double onsetT, double weightT) {
        onset = onsetT;
        weight = weightT;
        genForm();
    }
    public WeightedOnset() {
        genForm();
    }
    /**************************BASIC END*****************************/

    /**************************PROPERTY START****************************/
    public double getOnset() {
        return onset;
    }
    public double getWeight() {
        return weight;
    }
    public void setOnset(double onsetT) {
        onset = onsetT;
    }
    public void setWeight(double weightT) {
        weight = weightT;
    }
    /**************************PROPERTY END*****************************/

    /*********************** DEBUGGING START ************************/
    public void print() {
        System.out.println("WeightedNote:");
        System.out.println("Onset: " + onset);
        System.out.println("Weight: " + weight);
    }
    /*********************** DEBUGGING END   ************************/
}
