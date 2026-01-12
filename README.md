# Lookback Option Pricing: Monte Carlo & Analytic

> **Project developed for the "Computational Methods for Finance" course at Università di Verona.**
>
> **⚠️ Status:** *Code Complete & Submitted - Pending Academic Review.*

## Project Overview
This Java project focuses on the valuation of **Lookback Options**, exotic derivatives whose payoff depends on the maximum or minimum asset price observed during the option's life.

The software implements and compares two primary pricing methodologies: **Analytic Formulas** (exact solutions) and **Monte Carlo Simulation** (approximation). A key aspect of this project is the implementation of **Variance Reduction techniques** to improve the efficiency and accuracy of the stochastic estimators.

## Key Features
* **Analytic Pricing**: Implementation of closed-form solutions for benchmarking Lookback prices.
    * Supports both **Fixed Strike** and **Floating Strike** options (Call & Put), continuously and discretely monitored, utilizing the analytic formulas derived by **Broadie, Glasserman, and Kou (1999)**.
* **Monte Carlo Engine**: Robust simulation engine leveraging the **Finmath Library** to generate asset paths and compute payoffs at maturity.
* **Variance Reduction**: Implementation of **Control Variates** (specifically using Black-Scholes prices of vanilla options) to significantly reduce the standard error of the Monte Carlo estimator.
* **Convergence Analysis**: Tools to visualize and analyze the convergence rate of the simulation.

## Project Structure
The project follows the standard Maven directory structure:

* `it.univr.analyticprices`: Contains the class `AnalyticPrices` with exact formulas.
* `it.univr.montecarlo`: Contains the core logic for the Monte Carlo pricing engine, including abstract definitions and concrete implementations for Fixed/Floating strikes.
* `src/test/java/it/univr/montecarlo`: Contains unit tests and plotting to validate results and generate convergence graphs.

## Tech Stack
* **Java 17**
* **Maven**: Dependency management.
* **Finmath Lib**: Used for stochastic process generation (Geometric Brownian Motion) and random number sequences.
* **JFreeChart**: For generating convergence and error plots.

## Authors & Acknowledgments

**Development Team:**
* Lorenzo Visonà Dalla Pozza
* Alberto Oliva Medin
* Federico Alberighi

**Credits:**
* This project is based on a template/guidelines provided by **Prof. Andrea Mazzon**, specifically regarding the methods to evaluate continuously monitored options by analytic formulas.
* The implementation of discretely monitored **Analytic Formulas**, **Monte Carlo Logic**, and **Variance Reduction (Control Variates)** was developed by the team as part of the project work.

## References
* Broadie, M., Glasserman, P., & Kou, S. G. (1999). Connecting discrete and continuous path-dependent options. *Finance and Stochastics*, 3, 55-82.
