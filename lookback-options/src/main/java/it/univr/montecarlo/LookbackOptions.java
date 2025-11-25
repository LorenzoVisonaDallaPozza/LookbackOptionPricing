package it.univr.montecarlo;

import net.finmath.exception.CalculationException;
import net.finmath.montecarlo.assetderivativevaluation.AssetModelMonteCarloSimulationModel;
import net.finmath.montecarlo.assetderivativevaluation.products.AbstractAssetMonteCarloProduct;
import net.finmath.stochastic.RandomVariable;
import net.finmath.time.TimeDiscretization;

public abstract class LookbackOptions extends AbstractAssetMonteCarloProduct {
	
	
	protected double[] buildMonitoringTimes(int discretelyTimes, AssetModelMonteCarloSimulationModel model) {
		TimeDiscretization td = model.getTimeDiscretization();
        if (discretelyTimes == 0) {
            double[] times = new double[td.getNumberOfTimes()];
            for (int i = 0; i < td.getNumberOfTimes(); i++) {
                times[i] = td.getTime(i);
            }
            return times;
        }
        double[] times = new double[discretelyTimes + 1];
        for (int i = 0; i <= discretelyTimes; i++) {
            int index = (int) Math.round(i * (td.getNumberOfTimes() - 1) / (double) discretelyTimes);
            times[i] = td.getTime(index);
            }
		return times;
        }
	
	
	protected RandomVariable getMax(double[] discretizedTimes, AssetModelMonteCarloSimulationModel model, int underlyingIndex) throws CalculationException {
		RandomVariable maxValue = model.getRandomVariableForConstant(0.0);
		for(double currentTime : discretizedTimes) {
			RandomVariable underlying = model.getAssetValue(currentTime, underlyingIndex);
			maxValue = maxValue.floor(underlying);			
		}
		return maxValue;
	}
	
	
	protected RandomVariable getMin(double[] discretizedTimes, AssetModelMonteCarloSimulationModel model, int underlyingIndex) throws CalculationException {
		RandomVariable minValue = model.getRandomVariableForConstant(Integer.MAX_VALUE); //S_0? can it be a payoff?
		for(double currentTime : discretizedTimes) {
			RandomVariable underlying = model.getAssetValue(currentTime, underlyingIndex);
			minValue = minValue.cap(underlying);		
		}
		return minValue;
	}

}
