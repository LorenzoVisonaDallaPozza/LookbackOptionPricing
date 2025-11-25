package it.univr.montecarlo;

import net.finmath.exception.CalculationException;
import net.finmath.montecarlo.assetderivativevaluation.AssetModelMonteCarloSimulationModel;
//import net.finmath.montecarlo.assetderivativevaluation.products.AbstractAssetMonteCarloProduct;
import net.finmath.stochastic.RandomVariable;
//import net.finmath.time.TimeDiscretization;

public class LookbackCallFloatingStrike extends LookbackOptions {
	
	private double maturity;
	private int underlyingIndex;
	private int discretelyTimes;
	
	
	public LookbackCallFloatingStrike(double maturity, int discretelyTimes) {
		this.maturity=maturity;
		this.underlyingIndex=0;
		this.discretelyTimes=discretelyTimes;
	}
	
	public LookbackCallFloatingStrike(double maturity) {
		this.maturity=maturity;
		this.underlyingIndex=0;
		this.discretelyTimes=0;
	}
	
	public LookbackCallFloatingStrike(int underlyingIndex, double maturity) {
		this.maturity=maturity;
		this.underlyingIndex=underlyingIndex;
		this.discretelyTimes=0;
	}
	
	public LookbackCallFloatingStrike(double maturity, int underlyingIndex, int discretelyTimes) {
		this.maturity=maturity;
		this.underlyingIndex=underlyingIndex;
		this.discretelyTimes=discretelyTimes;
	}
	
	
	
	@Override
	public RandomVariable getValue(double evaluationTime, AssetModelMonteCarloSimulationModel model) throws CalculationException {
		double[] discretizedTimes = buildMonitoringTimes(discretelyTimes, model);
		/*
		RandomVariable minValue = model.getRandomVariableForConstant(Integer.MAX_VALUE);
		for(double currentTime : discretizedTimes) {
			RandomVariable underlying = model.getAssetValue(currentTime, underlyingIndex);
			
			RandomVariable term1 = minValue.add(underlying);                    
			RandomVariable term2 = minValue.sub(underlying).abs();
			RandomVariable term3 = term1.sub(term2);
			minValue = term3.div(2.0);	
		}
		*/
		RandomVariable minValue = getMin(discretizedTimes, model, underlyingIndex);
		RandomVariable finalValue = model.getAssetValue(model.getTimeIndex(maturity), underlyingIndex);
		RandomVariable values = finalValue.sub(minValue).floor(0.0);
		
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
