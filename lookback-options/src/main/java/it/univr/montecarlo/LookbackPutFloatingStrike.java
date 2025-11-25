package it.univr.montecarlo;

import net.finmath.exception.CalculationException;
import net.finmath.montecarlo.assetderivativevaluation.AssetModelMonteCarloSimulationModel;
//import net.finmath.montecarlo.assetderivativevaluation.products.AbstractAssetMonteCarloProduct;
import net.finmath.stochastic.RandomVariable;
//import net.finmath.time.TimeDiscretization;

public class LookbackPutFloatingStrike extends LookbackOptions {
	
	private double maturity;
	private int underlyingIndex;
	private int discretelyTimes;
	
	
	public LookbackPutFloatingStrike(double maturity, int discretelyTimes) {
		this.maturity=maturity;
		this.underlyingIndex=0;
		this.discretelyTimes=discretelyTimes;
	}
	
	public LookbackPutFloatingStrike(double maturity) {
		this.maturity=maturity;
		this.underlyingIndex=0;
		this.discretelyTimes=0;
	}
	
	public LookbackPutFloatingStrike(int underlyingIndex, double maturity) {
		this.maturity=maturity;
		this.underlyingIndex=underlyingIndex;
		this.discretelyTimes=0;
	}
	
	public LookbackPutFloatingStrike(double maturity, int underlyingIndex, int discretelyTimes) {
		this.maturity=maturity;
		this.underlyingIndex=underlyingIndex;
		this.discretelyTimes=discretelyTimes;
	}
	
	
	
	@Override
	public RandomVariable getValue(double evaluationTime, AssetModelMonteCarloSimulationModel model) throws CalculationException {
		double[] discretizedTimes = buildMonitoringTimes(discretelyTimes, model);
		/*
		RandomVariable maxValue = model.getRandomVariableForConstant(0.0);
		for(double currentTime : discretizedTimes) {
			RandomVariable underlying = model.getAssetValue(currentTime, underlyingIndex);
			
			RandomVariable term1 = maxValue.add(underlying);                    
			RandomVariable term2 = maxValue.sub(underlying).abs();
			RandomVariable term3 = term1.add(term2);
			maxValue = term3.div(2.0);
		}
		*/
		RandomVariable maxValue = getMax(discretizedTimes, model, underlyingIndex);
		RandomVariable finalValue = model.getAssetValue(model.getTimeIndex(maturity), underlyingIndex);
		RandomVariable values = maxValue.sub(finalValue).floor(0.0);
		
		// Discounting...
		final RandomVariable numeraireAtMaturity = model.getNumeraire(maturity);
		final RandomVariable monteCarloWeights = model.getMonteCarloWeights(maturity);
		values = values.div(numeraireAtMaturity).mult(monteCarloWeights);

		// ...to evaluation time.
		final RandomVariable numeraireAtEvalTime = model.getNumeraire(evaluationTime);
		final RandomVariable monteCarloWeightsAtEvalTime = model.getMonteCarloWeights(evaluationTime);
		values = values.mult(numeraireAtEvalTime).div(monteCarloWeightsAtEvalTime);
	
		return values;
	}
	
}

