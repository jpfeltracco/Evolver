package controllers;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Random;

import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.BasicLayer;

import evolver.Element;
import ui.Builder.MenuItems;
import ui.Builder.MenuItems.EntryType;
import util.StringHolder;
import util.Rand;

public class EncogMLP extends Controller {
    BasicNetwork net;

    @Override
    public double[] calculate(double... in) {
        double[] out = new double[net.getOutputCount()];
        net.compute(in, out);
        return out;
    }

    @Override
    public void setConfig(Element e) {
        element = e;
        net.decodeFromArray(e.config);
    }

    @Override
    public int getConfigSize() {
        return net.encodedArrayLength();
    }

    @Override
    public Element generateRandomConfig() {
        Element e = new Element();
        e.config = new double[net.encodedArrayLength()];
        for (int i = 0; i < net.encodedArrayLength(); i++) {
            e.config[i] = Rand.r.nextGaussian();
        }
        return e;
    }

    // TODO both mlps will share this behavior
    @Override
    public void mutateElement(Element e, float mutateAmt) {
        for (int i = 0; i < mutateAmt * e.config.length; i++) {
            e.config[(int) (Rand.r.nextFloat() * e.config.length)] = Rand.r.nextGaussian();
        }
    }

    // TODO same as other mlp, put in superclass
    @Override
    public boolean isSame(Element e1, Element e2) {
        double totalDist = 0;
        for (int i = 0; i < e1.config.length; i++)
            totalDist += Math.abs(e1.config[i] - e2.config[i]);
        totalDist /= e1.config.length;
        return totalDist < .10f;
    }

    @Override
    public void saveConfig(File loc) {
        try {
            PrintWriter pw = new PrintWriter(loc);
            double[] outArr = null;
            net.decodeFromArray(outArr);
            for (double d : outArr) {
                pw.println(d);
            }
            pw.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String[] getExtension() {
        return new String[] {"txt"};
    }

    StringHolder internalSize = new StringHolder("3, 3, 3");

    private int[] netDim;
    private int[] dims;

    @Override
    public boolean check() {
        ArrayList<Integer> internalSizeArray;
        //		if(!menu.checkAllInit())
        //			return false;

        String[] arr = internalSize.getValue().split(",");
        internalSizeArray = new ArrayList<Integer>(arr.length);
        for (int i = 0; i < arr.length; i++) {
            if (arr[i].trim().length() > 0) {
                try {
                    internalSizeArray.add(Integer.parseInt(arr[i].trim()));
                } catch (Exception e) {
                    System.out.println("Error in numerical input");
                    return false;
                }
            }
        }

        if (internalSizeArray.size() == 0) return false;

        netDim = new int[internalSizeArray.size()];
        for (int i = 0; i < internalSizeArray.size(); i++) {
            this.netDim[i] = internalSizeArray.get(i);
        }

        return true;
    }

    //Helper Methods:
    private int[] calculateDimArray() {
        int[] dims = new int[netDim.length + 2];
        dims[0] = numIn;
        dims[dims.length - 1] = numOut;
        for (int i = 1; i < dims.length - 1; i++) dims[i] = netDim[i - 1];
        return dims;
    }

    @Override
    public Controller copy() {
        return new EncogMLP();
    }

    @Override
    public boolean start(int numIn, int numOut) {
        dims = calculateDimArray();
        net = new BasicNetwork();
        for (int i : dims) net.addLayer(new BasicLayer(i));
        net.getStructure().finalizeStructure();
        net.reset();
        return true;
    }

    @Override
    public void menuInit(MenuItems menu) {
        menu.add("Net Dim", EntryType.TEXT, internalSize, false);
    }
}
