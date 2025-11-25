package it.univr.montecarlo;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;
import java.util.Arrays;
import java.util.Locale;
import java.util.function.DoubleUnaryOperator;
import java.util.stream.IntStream;

import it.univr.analyticprices.AnalyticPrices;
import net.finmath.exception.CalculationException;
import net.finmath.montecarlo.BrownianMotion;
import net.finmath.montecarlo.BrownianMotionFromMersenneRandomNumbers;
import net.finmath.montecarlo.assetderivativevaluation.MonteCarloBlackScholesModel;
import net.finmath.montecarlo.assetderivativevaluation.products.AbstractAssetMonteCarloProduct;
import net.finmath.plots.Named;
import net.finmath.plots.Plot2D;
import net.finmath.plots.Plotable2D;
import net.finmath.plots.PlotableFunction2D;
import net.finmath.plots.PlotablePoints2D;
import net.finmath.time.TimeDiscretization;
import net.finmath.time.TimeDiscretizationFromArray;

public class ThirdTest {
	
	public static void main(String[] args) throws CalculationException, InterruptedException {
		
		
		final DecimalFormat formatterDiscretizeTimes = new DecimalFormat("0.0");
		formatterDiscretizeTimes.setDecimalFormatSymbols(DecimalFormatSymbols.getInstance(Locale.ENGLISH));
		
		double spotPrice = 100.0;
		double riskFreeRate = 0.1;
		double volatility = 0.3;

		
		double maturity = 0.5;		
		double strike = 100.0;
		int timeSteps = 1;

		//time discretization parameters
		int numberOfTimeSteps = 1000;
		double initialTime = 0.0;
		double timeStep = maturity / numberOfTimeSteps;
		
		TimeDiscretization times = new TimeDiscretizationFromArray(initialTime, numberOfTimeSteps, timeStep);
		AbstractAssetMonteCarloProduct discretelyMonitoredCallFixedStrike = new LookbackCallFixedStrike(maturity, strike, timeSteps);
		AbstractAssetMonteCarloProduct continuoslyMonitoredCallFixedStrike = new LookbackCallFixedStrike(maturity, strike);
		double analyticPriceContinuouslyMonitoredCallFixed = AnalyticPrices.continuouslyMonitoredLookbackCallFixedStrike(spotPrice, riskFreeRate, volatility, maturity, strike);
		
		int numberOfPaths = 20000;
		int seed = 1897;
		BrownianMotion ourDriver = new BrownianMotionFromMersenneRandomNumbers(times, 1, numberOfPaths, seed);
		
		MonteCarloBlackScholesModel blackScholesProcess = new MonteCarloBlackScholesModel(spotPrice, riskFreeRate, volatility, ourDriver);
		
		
		final double maxNumberOfFixings = 1000.0; // Il limite superiore del tuo asse X
		final double numberOfInitialPoints = 0.0; // Iniziamo senza punti
		
		final double[] timeStepState = new double [200];
		final double[] optionPrices = new double [200];
		
		int index = 0;

		final double mCcontinuousPrice = continuoslyMonitoredCallFixedStrike.getValue(blackScholesProcess);
		final double analyticPrice = analyticPriceContinuouslyMonitoredCallFixed;
		DoubleUnaryOperator secondLine = x -> analyticPrice;
		DoubleUnaryOperator line = x -> mCcontinuousPrice;
		
		final PlotableFunction2D mCPriceLine = new PlotableFunction2D(
				numberOfInitialPoints, 
				maxNumberOfFixings, 
			    2, 
			    new Named<DoubleUnaryOperator>("MC continously monitored price", line),
			   null
			);
		
		final PlotableFunction2D analyticPriceLine = new PlotableFunction2D(
				numberOfInitialPoints, 
				maxNumberOfFixings, 
			    2, 
			    new Named<DoubleUnaryOperator>("Analytic price", secondLine),
			   null
			);
		
		
		
		final Plot2D lookbackPlot = new Plot2D(
				0.0, // X min (N=0)
			    maxNumberOfFixings, // X max (N=1000)
			    0, // Qui non serve numberOfPlottedValues, useremo PlotablePoints
			    Arrays.asList() // Iniziamo con una lista vuota di elementi plottabili
			);

			// AGGIUNGI QUESTO BLOCCO: Imposta gli assi e mostra la finestra
			final double maxOptionPrice = continuoslyMonitoredCallFixedStrike.getValue(blackScholesProcess); // Stima massima realistica, adatta se necessario

			lookbackPlot.setXAxisLabel("Number of monitoring times");
			lookbackPlot.setYAxisLabel("MC price of the Lookback fixed call");
			lookbackPlot.setYRange(9, maxOptionPrice+5); 
			lookbackPlot.show(); // <-- QUESTA RIGA APRE LA FINESTRA DEL GRAFICO!
		
		while (timeSteps < 1006) {

			discretelyMonitoredCallFixedStrike = new LookbackCallFixedStrike(maturity, strike, timeSteps);

			double lookbackPrice = discretelyMonitoredCallFixedStrike.getValue(blackScholesProcess);
			
			if (index >= timeStepState.length) {
		        // Interrompi il ciclo se i vettori sono pieni
		        System.out.println("Raggiunto il limite di punti plottabili.");
		        break; 
		    }
			
			// 3. Salvataggio dei Dati
		    timeStepState[index] = (double)timeSteps; // Asse X: Numero di Fixings (N)
		    optionPrices[index] = lookbackPrice;      // Asse Y: Prezzo V(N)

		    // 4. PREPARAZIONE E AGGIORNAMENTO DEL PLOT DINAMICO
		    
		    // a) Crea i vettori 'slice' contenenti solo i dati validi (da 0 a index)
		    final double[] currentXData = IntStream.range(0, index + 1)
		                                           .mapToDouble(i -> timeStepState[i])
		                                           .toArray();
		    final double[] currentYData = IntStream.range(0, index + 1)
		                                           .mapToDouble(i -> optionPrices[i])
		                                           .toArray();

		    
		    
		    // c) Crea l'oggetto Plotable2D usando il metodo statico .of()
		    final PlotablePoints2D pointsSeries = PlotablePoints2D.of(
		        "Prezzo MC Lookback", 
		        currentXData,         
		        currentYData,
		        null
		    );

		    // d) Aggiorna il grafico
		    final List<Plotable2D> plotables = Arrays.asList(pointsSeries, mCPriceLine, analyticPriceLine);
		    lookbackPlot.update(plotables);
		    
		    // 5. Incremento e Pausa
		    timeSteps += 10;
		    index++;
		    
		    // Pausa per visualizzare l'animazione (e.g., 50ms)
		    Thread.sleep(100); 
		}
		
		
		
		
	}

}
