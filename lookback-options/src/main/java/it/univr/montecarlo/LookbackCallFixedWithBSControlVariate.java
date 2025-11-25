package it.univr.montecarlo;

import net.finmath.exception.CalculationException;
import net.finmath.montecarlo.assetderivativevaluation.AssetModelMonteCarloSimulationModel;
import net.finmath.stochastic.RandomVariable;
//import net.finmath.functions.*;
import net.finmath.montecarlo.assetderivativevaluation.MonteCarloAssetModel;
import net.finmath.montecarlo.assetderivativevaluation.models.BlackScholesModel;
import it.univr.analyticprices.AnalyticPrices;

/**
 * 
 * Implements the valuation of a Lookback Call Fixed Strike Option with Control Variate.
 * 
 * 
 * 
 * 
 * 
 */

public class LookbackCallFixedWithBSControlVariate extends LookbackOptions {
	
	private double maturity;
	private double strike;
	private int discretelyTimes;
	private Integer underlyingIndex;

	public LookbackCallFixedWithBSControlVariate(double maturity, double strike, int discretelyTimes) {
		this.maturity=maturity;
		this.strike=strike;
		this.discretelyTimes=discretelyTimes;
		this.underlyingIndex=0;
	}
	
	
	public LookbackCallFixedWithBSControlVariate(double maturity, int underlyingIndex, double strike, int discretelyTimes) {
		this.maturity=maturity;
		this.underlyingIndex=underlyingIndex;
		this.strike=strike;
		this.discretelyTimes=discretelyTimes;
	}
	
	public double computeAnalyticValue(double evaluationTime, AssetModelMonteCarloSimulationModel model, 
			BlackScholesModel processModel) throws CalculationException {
		
		double spotPrice = model.getAssetValue(0, underlyingIndex).doubleValue();
		double riskFreeRate = processModel.getRiskFreeRate().doubleValue();
		double volatility = processModel.getVolatility().doubleValue();
		//return AnalyticFormulas.blackScholesOptionValue(spotPrice, riskFreeRate, volatility, maturity, strike, true);
		
		return AnalyticPrices.continuouslyMonitoredLookbackCallFixedStrike(spotPrice, riskFreeRate, volatility, maturity, strike);
	}
	
	@Override
	public RandomVariable getValue(double evaluationTime, AssetModelMonteCarloSimulationModel model) throws CalculationException {
		
		
		LookbackCallFixedStrike stdLoockBackCallFixedStrike = new LookbackCallFixedStrike(maturity, underlyingIndex, strike, discretelyTimes);
		
		RandomVariable Z = stdLoockBackCallFixedStrike.getValue(0.0, model);
		
		if(!(((MonteCarloAssetModel) model).getModel() instanceof BlackScholesModel)) {
			System.out.println("The model is not Black-Scholes: we are going to perform the standard Monte Carlo");
			return Z;
		}
		
		BlackScholesModel processModel = (BlackScholesModel) ((MonteCarloAssetModel) model).getModel();
		double blackSholesPrice = computeAnalyticValue(0.0, model, processModel);
		
		RandomVariable muY = model.getRandomVariableForConstant(blackSholesPrice);
		
		LookbackCallFixedStrike countinousLoockBackCallFixedStrike = new LookbackCallFixedStrike(maturity, underlyingIndex, strike);
		RandomVariable Y = countinousLoockBackCallFixedStrike.getValue(0.0, model);
		
		//RandomVariable Y = model.getAssetValue(maturity, underlyingIndex).sub(strike).floor(0.0).mult(Math.exp(-processModel.getRiskFreeRate().doubleValue()*maturity));
		
		double covariance = Z.covariance(Y).doubleValue();
		double variance = Y.variance().doubleValue();
		double optimalC = covariance/variance;
		
		RandomVariable termToSubtract = Y.sub(muY).mult(optimalC);
		
		RandomVariable Zc = Z.sub(termToSubtract);
		
		//System.out.println("Analytic formula: " + muY);
		//System.out.println("Mean of simulated process: " + Y.getAverage());
	
		return Zc;
	}
	
	

}
