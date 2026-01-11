package it.univr.montecarlo;



import it.univr.analyticprices.AnalyticPrices;
import net.finmath.exception.CalculationException;
import net.finmath.montecarlo.BrownianMotion;
import net.finmath.montecarlo.BrownianMotionFromMersenneRandomNumbers;
import net.finmath.montecarlo.assetderivativevaluation.MonteCarloBlackScholesModel;
import net.finmath.montecarlo.assetderivativevaluation.products.AbstractAssetMonteCarloProduct;
import net.finmath.time.TimeDiscretization;
import net.finmath.time.TimeDiscretizationFromArray;
import net.finmath.stochastic.RandomVariable;

public class ControlVariateTest {

	public static void main(String[] args) throws CalculationException {
		//model parameters
		double spotPrice = 100.0;
		double riskFreeRate = 0.1;
		double volatility = 0.3;

		//option parameters
		double maturity = 0.5;		
		double strike = 100.0;
		int numberOfFixingsForDiscretelyMonitoredLookbacks = 1000;

		//time discretization parameters
		int numberOfTimeSteps = 10000;
		double initialTime = 0.0;
		double timeStep = maturity / numberOfTimeSteps;
				
		TimeDiscretization times = new TimeDiscretizationFromArray(initialTime, numberOfTimeSteps, timeStep);
		AbstractAssetMonteCarloProduct continuouslyMonitoredCallFixedStrike = new LookbackCallFixedStrike(maturity, strike);
		AbstractAssetMonteCarloProduct discretelyMonitoredCallFixedStrike = new LookbackCallFixedStrike(maturity, strike, numberOfFixingsForDiscretelyMonitoredLookbacks);
		
		//simulation parameters
		int numberOfPaths = 20000;
		int seed = 1897;
		BrownianMotion ourDriver = new BrownianMotionFromMersenneRandomNumbers(times, 1, numberOfPaths, seed);
		
		MonteCarloBlackScholesModel blackScholesProcess = new MonteCarloBlackScholesModel(spotPrice, riskFreeRate, volatility, ourDriver);
		
		AbstractAssetMonteCarloProduct discretelyMonitoredCallFixedWithCV = new 
				LookbackCallFixedWithBSControlVariate(maturity, strike, numberOfFixingsForDiscretelyMonitoredLookbacks);
		
		double mcPriceDiscretelyMonitoredCallFixedWithCV = discretelyMonitoredCallFixedWithCV.getValue(blackScholesProcess);
		System.out.println("Control Variates: " + mcPriceDiscretelyMonitoredCallFixedWithCV);
		
		double mCPriceContinuouslyMonitoredCallFixed = continuouslyMonitoredCallFixedStrike.getValue(blackScholesProcess);
		double mCPriceDiscretelyMonitoredCallFixed = discretelyMonitoredCallFixedStrike.getValue(blackScholesProcess);
		
//		System.out.println("Call Fixed");
		System.out.println("Continous " + mCPriceContinuouslyMonitoredCallFixed);
		System.out.println("Discrete " + mCPriceDiscretelyMonitoredCallFixed);
		System.out.println("Analytic fromula: " + AnalyticPrices.discretelyMonitoredLookbackCallFixedStrike(spotPrice, riskFreeRate, volatility, maturity, strike, numberOfFixingsForDiscretelyMonitoredLookbacks));
		
		RandomVariable payoff = discretelyMonitoredCallFixedStrike.getValue(0.0, blackScholesProcess);
		System.out.println("Expected value of MC: " + payoff.getAverage());
		RandomVariable payoffCV = discretelyMonitoredCallFixedWithCV.getValue(0.0, blackScholesProcess);
		System.out.println("Expected value of CV: " + payoffCV.getAverage());
		
		System.out.println("Standard deviation of MC: " + payoff.getStandardDeviation());
		System.out.println("Standard deviation of CV: " + payoffCV.getStandardDeviation());
		System.out.println("Ratio: " + payoff.getStandardDeviation()/payoffCV.getStandardDeviation());
		
		
		
//		AbstractAssetMonteCarloProduct continuouslyMonitoredCallFloatingStrike = new LookbackCallFloatingStrike(maturity);
//		AbstractAssetMonteCarloProduct discretelyMonitoredCallFloatingStrike = new LookbackCallFloatingStrike(maturity, numberOfFixingsForDiscretelyMonitoredLookbacks);
		
//		double mCPriceContinuouslyMonitoredCallFloat = continuouslyMonitoredCallFloatingStrike.getValue(blackScholesProcess);
//		double mCPriceDiscretelyMonitoredCallFloat = discretelyMonitoredCallFloatingStrike.getValue(blackScholesProcess);
		
//		System.out.println("Call Floating");
//		System.out.println("Continous " + mCPriceContinuouslyMonitoredCallFloat);
//		System.out.println("Discrete " + mCPriceDiscretelyMonitoredCallFloat);
//		System.out.println("Analytic fromula: " + AnalyticPrices.continuouslyMonitoredLookbackCallFloatingStrike(spotPrice, riskFreeRate, volatility, maturity));
		
		
		
//		AbstractAssetMonteCarloProduct continuouslyMonitoredPutFixedStrike = new LookbackPutFixedStrike(maturity, strike);
//		AbstractAssetMonteCarloProduct discretelyMonitoredPutFixedStrike = new LookbackPutFixedStrike(maturity, strike, numberOfFixingsForDiscretelyMonitoredLookbacks);
		
//		double mCPriceContinuouslyMonitoredPutFixed = continuouslyMonitoredPutFixedStrike.getValue(blackScholesProcess);
//		double mCPriceDiscretelyMonitoredPutFixed = discretelyMonitoredPutFixedStrike.getValue(blackScholesProcess);
		
//		System.out.println("Put Fixed");
//		System.out.println("Continous " + mCPriceContinuouslyMonitoredPutFixed);
//		System.out.println("Discrete " + mCPriceDiscretelyMonitoredPutFixed);
//		System.out.println("Analytic fromula: " + AnalyticPrices.continuouslyMonitoredLookbackPutFixedStrike(spotPrice, riskFreeRate, volatility, maturity, strike));
		
		
		
//		AbstractAssetMonteCarloProduct continuouslyMonitoredPutFloatingStrike = new LookbackPutFloatingStrike(maturity);
//		AbstractAssetMonteCarloProduct discretelyMonitoredPutFloatingStrike = new LookbackPutFloatingStrike(maturity, numberOfFixingsForDiscretelyMonitoredLookbacks);
		
//		double mCPriceContinuouslyMonitoredPutFloat = continuouslyMonitoredPutFloatingStrike.getValue(blackScholesProcess);
//		double mCPriceDiscretelyMonitoredPutFloat = discretelyMonitoredPutFloatingStrike.getValue(blackScholesProcess);
		
//		System.out.println("Put Floating");
//		System.out.println("Continous " + mCPriceContinuouslyMonitoredPutFloat);
//		System.out.println("Discrete " + mCPriceDiscretelyMonitoredPutFloat);
//		System.out.println("Analytic fromula: " + AnalyticPrices.continuouslyMonitoredLookbackPutFloatingStrike(spotPrice, riskFreeRate, volatility, maturity));
		
		
		
	}

}
