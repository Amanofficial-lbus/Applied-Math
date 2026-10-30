package com.example.data

import com.example.data.model.ChapterEntity
import com.example.data.model.FormulaItem
import com.example.data.model.TopicEntity

object SyllabusCatalog {

    fun getChapters(): List<ChapterEntity> = listOf(
        ChapterEntity(
            id = 1,
            name = "Numbers, Quantification and Numerical Application",
            code = "CH-01",
            iconName = "numbers",
            colorHex = "#38BDF8",
            description = "Modulo arithmetic, alligation, boats & streams, pipes and cisterns, races",
            totalTopics = 5
        ),
        ChapterEntity(
            id = 2,
            name = "Numerical Inequalities",
            code = "CH-02",
            iconName = "alt_route",
            colorHex = "#22C55E",
            description = "Meaning, properties, single & two variable inequalities, modulus function",
            totalTopics = 5
        ),
        ChapterEntity(
            id = 3,
            name = "Matrices",
            code = "CH-03",
            iconName = "grid_view",
            colorHex = "#A855F7",
            description = "Types, operations, multiplication, transpose, symmetric & skew-symmetric matrices",
            totalTopics = 13
        ),
        ChapterEntity(
            id = 4,
            name = "Determinants",
            code = "CH-04",
            iconName = "grid_view",
            colorHex = "#EC4899",
            description = "Minors, cofactors, expansion, adjoint, inverse & Martin's matrix rule",
            totalTopics = 8
        ),
        ChapterEntity(
            id = 5,
            name = "Differentiation",
            code = "CH-05",
            iconName = "functions",
            colorHex = "#F59E0B",
            description = "Revision of 11th, logarithmic, parametric forms & higher order derivatives",
            totalTopics = 4
        ),
        ChapterEntity(
            id = 6,
            name = "Applications of Derivatives",
            code = "CH-06",
            iconName = "functions",
            colorHex = "#10B981",
            description = "Rate measure, increasing/decreasing, maxima/minima, marginal cost & revenue",
            totalTopics = 9
        ),
        ChapterEntity(
            id = 7,
            name = "Integrals",
            code = "CH-07",
            iconName = "functions",
            colorHex = "#06B6D4",
            description = "Indefinite & definite integrals, substitution, partial fractions, parts, CS & PS",
            totalTopics = 10
        ),
        ChapterEntity(
            id = 8,
            name = "Differential Equations",
            code = "CH-08",
            iconName = "functions",
            colorHex = "#8B5CF6",
            description = "Order and degree, general/particular solutions, formation & variable separable",
            totalTopics = 5
        ),
        ChapterEntity(
            id = 9,
            name = "Probability",
            code = "CH-09",
            iconName = "casino",
            colorHex = "#F97316",
            description = "Random variables, Binomial & Poisson distributions, Normal curve & Z-scores",
            totalTopics = 7
        ),
        ChapterEntity(
            id = 10,
            name = "Inferential Statistics",
            code = "CH-10",
            iconName = "show_chart",
            colorHex = "#14B8A6",
            description = "Sampling methods, standard error, CLT, confidence intervals, Z-test & t-tests",
            totalTopics = 12
        ),
        ChapterEntity(
            id = 11,
            name = "Time Based Data",
            code = "CH-11",
            iconName = "show_chart",
            colorHex = "#6366F1",
            description = "Components of time series, moving averages & least squares trend fitting",
            totalTopics = 5
        ),
        ChapterEntity(
            id = 12,
            name = "Financial Mathematics",
            code = "CH-12",
            iconName = "account_balance",
            colorHex = "#EC4899",
            description = "Perpetuity, sinking funds, bonds, EMI, CAGR & depreciation methods",
            totalTopics = 6
        ),
        ChapterEntity(
            id = 13,
            name = "Linear Programming",
            code = "CH-13",
            iconName = "alt_route",
            colorHex = "#84CC16",
            description = "LPP terminology, mathematical formulation, graphical corner points method",
            totalTopics = 4
        )
    )

    fun getTopics(today: String): List<TopicEntity> {
        val list = mutableListOf<TopicEntity>()
        var globalId = 1

        // Helper to add topic in fresh zero state
        fun addT(
            chapterId: Int,
            chapterName: String,
            code: String,
            name: String,
            notes: String,
            estMin: Int = 25
        ) {
            val isAssignedToday = (globalId <= 5)
            list.add(
                TopicEntity(
                    id = globalId++,
                    chapterId = chapterId,
                    chapterName = chapterName,
                    code = code,
                    name = name,
                    isCompleted = false,
                    isRollover = false,
                    dateAssigned = if (isAssignedToday) today else null,
                    estimatedMinutes = estMin,
                    notes = notes
                )
            )
        }

        // --- Ch 1: Numbers, Quantification and Numerical Application (5 Topics) ---
        addT(1, "Numbers & Quantification", "1.1", "Modulo Arithmetic", "Congruence modulo, Equivalence class, Properties, Modulo with exponent", 20)
        addT(1, "Numbers & Quantification", "1.2", "Alligation and Mixture", "Formula of alligation, Formula of repeated dilution", 18)
        addT(1, "Numbers & Quantification", "1.3", "Boats and Streams", "Upstream & downstream relative velocity problems", 20)
        addT(1, "Numbers & Quantification", "1.4", "Pipes and Cisterns", "Inlet & outlet filling/emptying rates and capacity", 20)
        addT(1, "Numbers & Quantification", "1.5", "Races and Games", "Head-starts, dead-heats, handicap calculations", 25)

        // --- Ch 2: Numerical Inequalities (5 Topics) ---
        addT(2, "Numerical Inequalities", "2.1", "Meaning of Numerical Inequalities", "Inequality signs, intervals and geometric representations", 20)
        addT(2, "Numerical Inequalities", "2.2", "Properties of Inequalities", "Addition, subtraction, multiplication & division with negatives", 20)
        addT(2, "Numerical Inequalities", "2.3", "Inequalities in One Variable", "Algebraic solution sets and number line representation", 25)
        addT(2, "Numerical Inequalities", "2.4", "Inequalities in Two Variables", "Graphical representation of half-planes", 25)
        addT(2, "Numerical Inequalities", "2.5", "Modulus Function and Inequalities", "Solving |x| < a and |x| > a inequality systems", 30)

        // --- Ch 3: Matrices (13 Topics) ---
        addT(3, "Matrices", "3.1", "Meaning and Order of Matrices", "Row & column dimensions, element notation a_ij", 20)
        addT(3, "Matrices", "3.2", "Types of Matrices", "Row, column, zero, square, diagonal, scalar & identity", 20)
        addT(3, "Matrices", "3.3", "Operations on Matrices", "Matrix addition, subtraction & equality conditions", 20)
        addT(3, "Matrices", "3.4", "Multiplication of Matrices", "Compatibility rule, row-by-column dot products", 25)
        addT(3, "Matrices", "3.5", "Properties of Matrix Multiplication", "Non-commutativity, associativity & distributivity", 25)
        addT(3, "Matrices", "3.6", "Multiplication by a Scalar", "Scalar k multiplied across all elements", 20)
        addT(3, "Matrices", "3.7", "Power of a Square Matrix", "A^2, A^3 and mathematical induction proofs", 25)
        addT(3, "Matrices", "3.8", "Matrix Polynomial", "f(A) evaluation for given polynomial f(x)", 25)
        addT(3, "Matrices", "3.9", "Transpose of a Matrix", "A^T interchange of rows and columns", 20)
        addT(3, "Matrices", "3.10", "Properties of Transpose", "(A+B)^T = A^T+B^T, (AB)^T = B^T A^T reversal law", 25)
        addT(3, "Matrices", "3.11", "Symmetric Matrices", "Condition A^T = A and real diagonal properties", 20)
        addT(3, "Matrices", "3.12", "Skew-Symmetric Matrices", "Condition A^T = -A and zero main diagonal elements", 20)
        addT(3, "Matrices", "3.13", "Sum of Symmetric and Skew-Symmetric", "Decomposition A = 1/2(A+A^T) + 1/2(A-A^T)", 25)

        // --- Ch 4: Determinants (8 Topics) ---
        addT(4, "Determinants", "4.1", "Meaning and Value of Determinants", "Square matrix scalar determinant |A|", 20)
        addT(4, "Determinants", "4.2", "Minors and Cofactors", "Minor M_ij and cofactor C_ij = (-1)^(i+j) M_ij", 25)
        addT(4, "Determinants", "4.3", "Expansion of Determinant", "Expansion along any row or column (Laplace)", 25)
        addT(4, "Determinants", "4.4", "Properties of Determinants", "Row switching, identical rows, scalar factoring", 30)
        addT(4, "Determinants", "4.5", "Adjoint of a Matrix & Properties", "Transpose of cofactor matrix: A · adj(A) = |A| I", 30)
        addT(4, "Determinants", "4.6", "Singular and Inverse Matrix", "Singular |A|=0 vs non-singular invertible matrices", 25)
        addT(4, "Determinants", "4.7", "Properties of Inverse of a Matrix", "(A^-1)^-1 = A, (AB)^-1 = B^-1 A^-1", 25)
        addT(4, "Determinants", "4.8", "Matrix Method / Martin's Rule", "Solving linear systems AX = B via X = A^-1 B", 35)

        // --- Ch 5: Differentiation (4 Topics) ---
        addT(5, "Differentiation", "5.1", "Revision of 11th Differentiation", "Standard power, trigonometric & exponential derivatives", 25)
        addT(5, "Differentiation", "5.2", "Logarithmic Differentiation", "Differentiating variable powers y = u(x)^v(x)", 30)
        addT(5, "Differentiation", "5.3", "Parametric Forms Differentiation", "dy/dx = (dy/dt) / (dx/dt)", 25)
        addT(5, "Differentiation", "5.4", "Higher Order Derivatives", "Second order derivative d^2y/dx^2 calculation", 30)

        // --- Ch 6: Applications of Derivatives (9 Topics) ---
        addT(6, "Applications of Derivatives", "6.1", "Derivative as a Rate Measure", "dy/dt rate of change with respect to time", 25)
        addT(6, "Applications of Derivatives", "6.2", "Increasing and Decreasing Functions", "Interval analysis where f'(x) > 0 or f'(x) < 0", 30)
        addT(6, "Applications of Derivatives", "6.3", "Maxima and Minima Values", "Absolute extreme values on closed intervals", 30)
        addT(6, "Applications of Derivatives", "6.4", "Local Maxima & Minima Critical Points", "First derivative test: f'(c) = 0 and sign change", 30)
        addT(6, "Applications of Derivatives", "6.5", "Local Extrema Cases and Tests", "Second derivative test: f''(c) sign evaluation", 30)
        addT(6, "Applications of Derivatives", "6.6", "Marginal Cost & Marginal Revenue", "MC = dC/dx, MR = dR/dx in economic models", 25)
        addT(6, "Applications of Derivatives", "6.7", "Fixed, Variable & Average Cost", "TC = FC + VC(x), AC = TC/x cost functions", 25)
        addT(6, "Applications of Derivatives", "6.8", "Demand, Revenue & Breakeven Analysis", "Profit P(x) = R(x) - C(x), Breakeven R(x)=C(x)", 30)
        addT(6, "Applications of Derivatives", "6.9", "Marginal Functions (MC, MAC, MC & AC)", "Relation between MC and AC at minimum average cost", 30)

        // --- Ch 7: Integrals (10 Topics) ---
        addT(7, "Integrals", "7.1", "Indefinite Integrals & Primitive Concept", "Anti-derivative definition, constant of integration", 25)
        addT(7, "Integrals", "7.2", "Elementary Standard Integrals", "Power rule, logarithmic & exponential integrals", 25)
        addT(7, "Integrals", "7.3", "Properties of Indefinite Integrals", "Linearity, scalar multiplication & sum rules", 25)
        addT(7, "Integrals", "7.4", "Integration by Substitution", "Variable transformation u = g(x) and du = g'(x)dx", 30)
        addT(7, "Integrals", "7.5", "Integration by Partial Fractions", "Linear & quadratic irreducible denominator decomposition", 35)
        addT(7, "Integrals", "7.6", "Some More Standard Integrals", "Integrals involving quadratic square roots", 30)
        addT(7, "Integrals", "7.7", "Integration by Parts & ILATE Rule", "∫ u dv = uv - ∫ v du with ILATE priority ordering", 30)
        addT(7, "Integrals", "7.8", "Special Exponential Form & Definite Integrals", "∫ e^x [f(x)+f'(x)] dx and Fundamental Theorem of Calculus", 35)
        addT(7, "Integrals", "7.9", "Application of Integrals - Bounded Areas", "Area between curves, symmetric closed regions", 35)
        addT(7, "Integrals", "7.10", "Integrals in Commerce (CS & PS)", "Consumer Surplus & Producer Surplus from demand/supply curves", 35)

        // --- Ch 8: Differential Equations (5 Topics) ---
        addT(8, "Differential Equations", "8.1", "Introduction and Basic Concepts", "Ordinary diff eq, dependent & independent variables", 20)
        addT(8, "Differential Equations", "8.2", "Order and Degree of Diff Equations", "Highest derivative order and power on polynomial forms", 25)
        addT(8, "Differential Equations", "8.3", "Solution of a Differential Equation", "General solutions with arbitrary constants vs particular solutions", 25)
        addT(8, "Differential Equations", "8.4", "Formation of Differential Equations", "Eliminating arbitrary constants equal to diff eq order", 30)
        addT(8, "Differential Equations", "8.5", "Variables Separable Technique", "Rearranging f(x)dx = g(y)dy and direct integration", 30)

        // --- Ch 9: Probability (7 Topics) ---
        addT(9, "Probability", "9.1", "Random Variables", "Discrete vs continuous sample space mappings", 20)
        addT(9, "Probability", "9.2", "Probability Distribution of Random Variables", "Probability Mass Function PMF and ΣP(x)=1 condition", 25)
        addT(9, "Probability", "9.3", "Mean and Variance of Probability Distribution", "E(X) = Σ xP(x) and Var(X) = E(X^2) - [E(X)]^2", 30)
        addT(9, "Probability", "9.4", "Binomial Experiment & Distribution", "Bernoulli trials, P(X=k) = nCk p^k q^(n-k)", 30)
        addT(9, "Probability", "9.5", "Poisson Distribution", "Limiting form of Binomial, parameter λ, P(X=k)=(λ^k e^-λ)/k!", 30)
        addT(9, "Probability", "9.6", "Continuous Probability Distribution", "Probability density function (PDF) and area under curve", 25)
        addT(9, "Probability", "9.7", "Normal Distribution & Z-Score", "Bell-shaped curve, standard normal variable Z = (X - μ)/σ", 35)

        // --- Ch 10: Inferential Statistics (12 Topics) ---
        addT(10, "Inferential Statistics", "10.1", "Population and Sample", "Census vs sample survey, sampling frame & units", 20)
        addT(10, "Inferential Statistics", "10.2", "Sampling Methods", "Probability (simple, systematic, stratified) & non-probability", 25)
        addT(10, "Inferential Statistics", "10.3", "Representative & Non-Representative Samples", "Biases, pitfalls and selection validity", 20)
        addT(10, "Inferential Statistics", "10.4", "Sampling Errors & Point Estimation", "Sampling vs non-sampling errors, sample mean x̄ estimator", 25)
        addT(10, "Inferential Statistics", "10.5", "Unbiased and Biased Sampling", "Mathematical expectation E(x̄) = μ property", 25)
        addT(10, "Inferential Statistics", "10.6", "Sampling Distribution & Standard Error", "Standard Error of the Mean (SEM) σx̄ = σ/√n", 30)
        addT(10, "Inferential Statistics", "10.7", "Central Limit Theorem (CLT)", "Approaching normality for sample size n ≥ 30", 30)
        addT(10, "Inferential Statistics", "10.8", "Interval Estimation", "Confidence intervals: x̄ ± Z_α/2 · (σ/√n)", 30)
        addT(10, "Inferential Statistics", "10.9", "Hypothesis Testing Basics", "Null H0 vs Alt Ha, Type I (α) and Type II (β) errors", 30)
        addT(10, "Inferential Statistics", "10.10", "Hypothesis Test for Mean (σ Known)", "Z-test critical region approach, one/two-tailed rejection rules", 35)
        addT(10, "Inferential Statistics", "10.11", "T-test & T-Distribution", "Student's t-curve, degrees of freedom df = n - 1", 30)
        addT(10, "Inferential Statistics", "10.12", "One Sample t-Test (σ Unknown)", "Test statistic t = (x̄ - μ) / (s/√n) using t-tables", 35)

        // --- Ch 11: Time Based Data (5 Topics) ---
        addT(11, "Time Based Data", "11.1", "Time Series", "Chronological data sequences in economics and business", 20)
        addT(11, "Time Based Data", "11.2", "Components of Time Series", "Trend (T), Cyclical (C), Seasonal (S), Irregular (I)", 25)
        addT(11, "Time Based Data", "11.3", "Models of Time Series Analysis", "Additive Y = T+C+S+I vs Multiplicative Y = T×C×S×I models", 25)
        addT(11, "Time Based Data", "11.4", "Method of Moving Averages", "3-yearly, 5-yearly & 4-yearly centered moving averages", 30)
        addT(11, "Time Based Data", "11.5", "Method of Least Squares", "Fitting linear trend line y = a + bx when Σx = 0", 35)

        // --- Ch 12: Financial Mathematics (6 Topics) ---
        addT(12, "Financial Mathematics", "12.1", "Perpetuity", "Present Value PV = R/i (ordinary) and PV = R/(i-g) (growing)", 25)
        addT(12, "Financial Mathematics", "12.2", "Sinking Funds", "Periodic payment calculation for future target capital", 25)
        addT(12, "Financial Mathematics", "12.3", "Bonds and Bond Valuation", "Present value of coupon payments + face value redemption", 30)
        addT(12, "Financial Mathematics", "12.4", "E.M.I.", "Equated Monthly Installment via reducing balance formula", 30)
        addT(12, "Financial Mathematics", "12.5", "CAGR", "Compound Annual Growth Rate geometric formula", 25)
        addT(12, "Financial Mathematics", "12.6", "Depreciation", "Straight Line vs Written Down Value (WDV) methods", 25)

        // --- Ch 13: Linear Programming (4 Topics) ---
        addT(13, "Linear Programming", "13.1", "Introduction & Terminology", "Objective function, decision variables, feasible regions", 20)
        addT(13, "Linear Programming", "13.2", "Mathematical Formulation", "Setting linear inequalities for maximization & minimization", 25)
        addT(13, "Linear Programming", "13.3", "Graphical Method", "Corner points method, plotting constraint boundary lines", 30)
        addT(13, "Linear Programming", "13.4", "Types of LPP Problems", "Unique optimal, multiple optimal, unbounded & infeasible", 30)

        return list
    }

    fun getFormulas(): List<FormulaItem> = listOf(
        FormulaItem(
            id = "f_bayes",
            title = "Bayes' Theorem",
            category = "Probability",
            formulaDisplay = "P(A | B) = [P(B | A) · P(A)] / P(B)",
            latexCode = "P(A \\mid B) = \\frac{P(B \\mid A) \\cdot P(A)}{P(B)}",
            explanation = "Relates conditional and marginal probabilities of stochastic events A and B. Essential for posterior updating given prior evidence.",
            complexity = "O(k)",
            isStarred = true,
            isHighYield = true,
            drillSteps = listOf(
                "Identify Prior Probability P(A)",
                "Identify Likelihood P(B|A)",
                "Compute Total Evidence P(B) = Σ P(B|Ai)·P(Ai)",
                "Divide numerator by total evidence"
            ),
            sampleProblem = "Given P(A) = 0.01, P(B|A) = 0.95, P(B|A') = 0.05. Find P(A|B).",
            sampleSolution = "P(A|B) = (0.95 × 0.01) / [(0.95 × 0.01) + (0.05 × 0.99)] = 0.161 (16.1%)"
        ),
        FormulaItem(
            id = "f_mod_exp",
            title = "Modulo Exponentiation",
            category = "Numbers & Quantification",
            formulaDisplay = "c ≡ a^b (mod m), where 0 ≤ c < m",
            latexCode = "c \\equiv a^b \\pmod{m}, \\quad 0 \\le c < m",
            explanation = "Binary modular exponentiation for rapid calculation of large integer powers. Solves remainder calculations without catastrophic overflow.",
            complexity = "O(log b)",
            isStarred = true,
            isHighYield = true,
            drillSteps = listOf(
                "Binary Decomposition: Convert exponent b into base-2 bits: b = Σ bi · 2^i",
                "Repeated Squaring Table: a^(2^k) ≡ (a^(2^(k-1)))^2 (mod m)",
                "Accumulate Modulo Residues: Multiply result by table residue for each active bit 1"
            ),
            sampleProblem = "Evaluate 3^35 mod 7",
            sampleSolution = "35 = 32 + 2 + 1. 3^1 ≡ 3, 3^2 ≡ 2, 3^4 ≡ 4, 3^8 ≡ 2, 3^16 ≡ 4, 3^32 ≡ 2 (mod 7). 3^35 ≡ 2 × 2 × 3 ≡ 12 ≡ 5 (mod 7)."
        ),
        FormulaItem(
            id = "f_taylor",
            title = "Taylor Series Expansion",
            category = "Differentiation",
            formulaDisplay = "f(x) = Σ [f^(n)(a) / n!] · (x - a)^n",
            latexCode = "f(x) = \\sum_{n=0}^{\\infty} \\frac{f^{(n)}(a)}{n!} (x - a)^n",
            explanation = "Polynomial approximation of an infinitely differentiable function near point a using its successive higher-order derivatives.",
            complexity = "O(n)",
            isStarred = true,
            isHighYield = true,
            drillSteps = listOf(
                "Compute successive derivatives f'(a), f''(a), f'''(a)...",
                "Divide each by factorial n!",
                "Scale by power (x - a)^n",
                "Truncate at desired degree of accuracy"
            ),
            sampleProblem = "Find 2nd-degree Taylor expansion of ln(1+x) about a = 0.",
            sampleSolution = "f(0) = 0, f'(0) = 1, f''(0) = -1. Hence ln(1+x) ≈ x - x²/2."
        ),
        FormulaItem(
            id = "f_lhopital",
            title = "L'Hôpital's Rule",
            category = "Differentiation",
            formulaDisplay = "lim[x→c] f(x)/g(x) = lim[x→c] f'(x)/g'(x)",
            latexCode = "\\lim_{x \\to c} \\frac{f(x)}{g(x)} = \\lim_{x \\to c} \\frac{f'(x)}{g'(x)}",
            explanation = "Resolves indeterminate forms [0/0] or [∞/∞] by taking derivatives of numerator and denominator independently.",
            complexity = "O(1)",
            isStarred = false,
            isHighYield = true
        ),
        FormulaItem(
            id = "f_int_parts",
            title = "Integration by Parts",
            category = "Integrals",
            formulaDisplay = "∫ u dv = u·v - ∫ v du",
            latexCode = "\\int u \\, dv = u v - \\int v \\, du",
            explanation = "Fundamental integration technique derived from product rule. Order of choice for u follows ILATE mnemonic (Inverse, Log, Algebraic, Trig, Exp).",
            complexity = "O(1)",
            isStarred = true,
            isHighYield = true
        ),
        FormulaItem(
            id = "f_consumer_surplus",
            title = "Consumer's Surplus (CS)",
            category = "Integrals",
            formulaDisplay = "CS = ∫[0 to x0] p(x) dx - p0·x0",
            latexCode = "CS = \\int_{0}^{x_0} p(x)\\,dx - p_0 x_0",
            explanation = "Measures total economic benefit received by consumers willing to pay more than market equilibrium price p0 for quantity x0.",
            complexity = "O(1)",
            isStarred = false,
            isHighYield = true
        ),
        FormulaItem(
            id = "f_producer_surplus",
            title = "Producer's Surplus (PS)",
            category = "Integrals",
            formulaDisplay = "PS = p0·x0 - ∫[0 to x0] g(x) dx",
            latexCode = "PS = p_0 x_0 - \\int_{0}^{x_0} g(x)\\,dx",
            explanation = "Measures total producer welfare received above the marginal cost supply curve g(x) up to equilibrium quantity x0.",
            complexity = "O(1)",
            isStarred = false,
            isHighYield = true
        ),
        FormulaItem(
            id = "f_cramer",
            title = "Matrix Inversion Method (Martin's Rule)",
            category = "Determinants",
            formulaDisplay = "X = A^(-1) · B = (1 / |A|) · adj(A) · B",
            latexCode = "X = A^{-1} B = \\frac{1}{|A|} \\operatorname{adj}(A) B",
            explanation = "Solves linear equation systems AX = B provided coefficient matrix determinant |A| ≠ 0.",
            complexity = "O(n^3)",
            isStarred = true,
            isHighYield = true
        ),
        FormulaItem(
            id = "f_poisson",
            title = "Poisson PMF",
            category = "Probability",
            formulaDisplay = "P(X = k) = (λ^k · e^(-λ)) / k!",
            latexCode = "P(X = k) = \\frac{\\lambda^k e^{-\\lambda}}{k!}",
            explanation = "Discrete probability distribution expressing probability of given number of independent events occurring in fixed time interval with known rate λ.",
            complexity = "O(k)",
            isStarred = true,
            isHighYield = true
        ),
        FormulaItem(
            id = "f_binomial",
            title = "Binomial Distribution",
            category = "Probability",
            formulaDisplay = "P(X = k) = nCk · p^k · (1-p)^(n-k)",
            latexCode = "P(X = k) = \\binom{n}{k} p^k (1-p)^{n-k}",
            explanation = "Calculates probability of exactly k successes in n independent Bernoulli trials with constant success probability p.",
            complexity = "O(k)",
            isStarred = false,
            isHighYield = true
        ),
        FormulaItem(
            id = "f_emi",
            title = "EMI (Reducing Balance)",
            category = "Financial Mathematics",
            formulaDisplay = "EMI = [P · r · (1+r)^n] / [(1+r)^n - 1]",
            latexCode = "EMI = \\frac{P \\cdot r (1+r)^n}{(1+r)^n - 1}",
            explanation = "Equated Monthly Installment for loan principal P over n months at monthly interest rate r.",
            complexity = "O(1)",
            isStarred = true,
            isHighYield = true
        ),
        FormulaItem(
            id = "f_cagr",
            title = "Compound Annual Growth Rate",
            category = "Financial Mathematics",
            formulaDisplay = "CAGR = (V_final / V_begin)^(1/t) - 1",
            latexCode = "CAGR = \\left(\\frac{V_{\\text{final}}}{V_{\\text{begin}}}\\right)^{1/t} - 1",
            explanation = "Mean annualized geometric growth rate that provides smoothed rate of return over multiple periods t.",
            complexity = "O(1)",
            isStarred = false,
            isHighYield = true
        ),
        FormulaItem(
            id = "f_perpetuity",
            title = "Present Value of Perpetuity",
            category = "Financial Mathematics",
            formulaDisplay = "PV = R / i  (Growing: PV = R / (i - g))",
            latexCode = "PV = \\frac{R}{i} \\quad \\left(\\text{Growing: } PV = \\frac{R}{i - g}\\right)",
            explanation = "Present value of constant periodic cash flow R continuing infinitely discounted at rate i with growth g.",
            complexity = "O(1)",
            isStarred = false,
            isHighYield = true
        ),
        FormulaItem(
            id = "f_least_squares",
            title = "Least Squares Linear Trend",
            category = "Time Based Data",
            formulaDisplay = "y = a + bx, where b = Σxy/Σx², a = Σy/N",
            latexCode = "y = a + bx, \\quad b = \\frac{\\sum xy}{\\sum x^2}, \\quad a = \\frac{\\sum y}{N}",
            explanation = "Calculates line of best fit for time series where origin x=0 is shifted to median period so Σx = 0.",
            complexity = "O(N)",
            isStarred = false,
            isHighYield = true
        ),
        FormulaItem(
            id = "f_z_score",
            title = "Standard Normal Z-Score",
            category = "Inferential Statistics",
            formulaDisplay = "Z = (X - μ) / σ  (Sample Mean: Z = (X̄ - μ)/(σ/√n))",
            latexCode = "Z = \\frac{X - \\mu}{\\sigma} \\quad \\left(\\text{Test: } Z = \\frac{\\bar{X} - \\mu}{\\sigma / \\sqrt{n}}\\right)",
            explanation = "Standardizes normal distribution variate to zero mean and unit variance for hypothesis testing and p-value lookup.",
            complexity = "O(1)",
            isStarred = false,
            isHighYield = true
        ),
        FormulaItem(
            id = "f_t_score",
            title = "One Sample t-Statistic",
            category = "Inferential Statistics",
            formulaDisplay = "t = (X̄ - μ) / (s / √n), df = n - 1",
            latexCode = "t = \\frac{\\bar{X} - \\mu}{s / \\sqrt{n}}, \\quad \\text{df} = n - 1",
            explanation = "Student's t-test for sample mean when population standard deviation σ is unknown and estimated by sample s.",
            complexity = "O(1)",
            isStarred = true,
            isHighYield = true
        )
    )
}
