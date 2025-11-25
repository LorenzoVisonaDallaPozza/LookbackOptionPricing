package it.univr.montecarlo;

import net.finmath.exception.CalculationException;
import net.finmath.montecarlo.assetderivativevaluation.AssetModelMonteCarloSimulationModel;
//import net.finmath.montecarlo.assetderivativevaluation.products.AbstractAssetMonteCarloProduct;
import net.finmath.stochastic.RandomVariable;
//import net.finmath.time.TimeDiscretization;

public class LookbackPutFixedStrike extends LookbackOptions {
	
	private double maturity;
	private double strike;
	private int underlyingIndex;
	private int discretelyTimes;
	
	
	public LookbackPutFixedStrike(double maturity, double strike, int discretelyTimes) {
		this.maturity=maturity;
		this.strike=strike;
		this.underlyingIndex=0;
		this.discretelyTimes=discretelyTimes;
	}
	
	public LookbackPutFixedStrike(double maturity, double strike) {
		this.maturity=maturity;
		this.strike=strike;
		this.underlyingIndex=0;
		this.discretelyTimes=0;
	}
	
	public LookbackPutFixedStrike(double maturity, int underlyingIndex, double strike) {
		this.maturity=maturity;
		this.strike=strike;
		this.underlyingIndex=underlyingIndex;
		this.discretelyTimes=0;
	}
	
	public LookbackPutFixedStrike(double maturity, int underlyingIndex, double strike, int discretelyTimes) {
		this.maturity=maturity;
		this.strike=strike;
		this.underlyingIndex=underlyingIndex;
		this.discretelyTimes=discretelyTimes;
	}
	
	
	
	@Override
	public RandomVariable getValue(double evaluationTime, AssetModelMonteCarloSimulationModel model) throws CalculationException {
		double[] discretizedTimes = buildMonitoringTimes(discretelyTimes, model);
		/*
		RandomVariable minValue = model.getRandomVariableForConstant(1000000000);
		for(double currentTime : discretizedTimes) {
			RandomVariable underlying = model.getAssetValue(currentTime, underlyingIndex);
			
			RandomVariable term1 = minValue.add(underlying);                    
			RandomVariable term2 = minValue.sub(underlying).abs();
			RandomVariable term3 = term1.sub(term2);
			minValue = term3.div(2.0);
		}
		*/
		RandomVariable minValue = getMin(discretizedTimes, model, underlyingIndex);
		RandomVariable values = minValue.sub(strike).mult(-1.0).floor(0.0);
		
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