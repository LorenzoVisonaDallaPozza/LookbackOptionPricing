package it.univr.montecarlo;

import net.finmath.exception.CalculationException;
import net.finmath.montecarlo.assetderivativevaluation.AssetModelMonteCarloSimulationModel;
import net.finmath.stochastic.RandomVariable;


public class LookbackCallFixedStrike extends LookbackOptions {
	
	private double maturity;
	private double strike;
	private int underlyingIndex;
	private int discretelyTimes;
	
	
	public LookbackCallFixedStrike(double maturity, double strike, int discretelyTimes) {
		this.maturity=maturity;
		this.strike=strike;
		this.underlyingIndex=0;
		this.discretelyTimes=discretelyTimes;
	}
	
	public LookbackCallFixedStrike(double maturity, double strike) {
		this.maturity=maturity;
		this.strike=strike;
		this.underlyingIndex=0;
		this.discretelyTimes=0;
	}
	
	public LookbackCallFixedStrike(double maturity, int underlyingIndex, double strike) {
		this.maturity=maturity;
		this.strike=strike;
		this.underlyingIndex=underlyingIndex;
		this.discretelyTimes=0;
	}
	
	public LookbackCallFixedStrike(double maturity, int underlyingIndex, double strike, int discretelyTimes) {
		this.maturity=maturity;
		this.strike=strike;
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
		RandomVariable values = maxValue.sub(strike).floor(0.0);
		
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
