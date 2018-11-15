# Problem 1

PD = 0.025
rho = 0.22
alpha = 0.995
L <- 10
NumLoans <- 1000
LGD <- 1
EAD <- 150

SimCopula <- function(Num, Size){
  N = 10000
  Iter_loss = array(0, dim = c(N))
  
  for (iter in 1:N){
    F = matrix(rnorm(1), 1, Num)
    Z = matrix(rnorm(Num, mean=0, sd=1),1, Num)
    U = sqrt(rho)*F + sqrt(1-rho^2)*Z
    Default = (U<qnorm(PD))
    loan_loss = Default*Size*LGD
    Iter_loss[iter] = sum(loan_loss)
  }
  
  #hist(Iter_loss)
  #EL = mean(Iter_loss)
  VaR = quantile(Iter_loss, alpha)
  return(VaR)
}

# Problem 1.1

WCDR <- pnorm((qnorm(PD) + sqrt(rho)*qnorm(alpha))/sqrt(1-rho))
Var11 <- NumLoans*L*LGD*(WCDR-PD)
ULVaR = EAD*LGD*(WCDR-PD)

x = pnorm((sqrt(1-0.22)*qnorm(0.133) - qnorm(0.025))/sqrt(0.22))

# Problem 1.2

Var12 <- SimCopula(1000, 10000)

# Problem 1.3

Var13 <- SimCopula(100, 100000)

# Problem 1.4

Var14 <- SimCopula(10, 1000000)

# Problem 2

rho = 0.15
Num <- 1000
L <- 10000
rating <- c("AAA", "AA", "A", "BBB", "BB", "B", "CCC or less", "Default")
libor <- 0.5
couponRate <- (0.5+2.1)/100
coupon <- couponRate*L
spreadToLibor <- c(0.7, 0.88, 1.19, 2.10, 3.39, 4.56, 8.17)
transProb <- c(0.05, 0.19, 4.79, 89.41, 4.35, 0.82, 0.2, 0.19)
netRate <- (spreadToLibor + libor)/100
cumProb <- rev(cumsum(rev(transProb)))
bounds <- qnorm(cumProb/100)

getIndex <- function(bounds, x){
  new_bounds <- append(bounds, x)
  new_bounds <- sort(new_bounds, decreasing = TRUE)
  ind <- match(x, new_bounds)
  return(ind)
}

N = 1000
Iter_loanValue = array(0, dim = c(N))

for (iter in 1:N){
  F <- matrix(rnorm(1), 1, Num)
  Z <- matrix(rnorm(Num, mean=0, sd=1),1, Num)
  U <- sqrt(rho)*F + sqrt(1-rho^2)*Z
  indexes <- lapply(U, function(x)getIndex(bounds, x))
  indexes <- unlist(indexes)
  newLoanValue <- ifelse(indexes==9, 0, (L+coupon)/(1+netRate[indexes-1]))
  Iter_loanValue[iter] <- sum(newLoanValue)
}

VaR <- L*Num - quantile(Iter_loanValue, 0.001)
