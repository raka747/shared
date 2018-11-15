Num <- 1000 #Number of loans

Size <- 10000 #Dollar size of each loan [$]

L <- Size * Num # Loan Principal 10000 * 1000 $ = 10 mio

LGD <- 0.45 # Loss Given Default

PD <- 0.0019 # Probability of Default

rho <- 0.15

alpha <- 0.999



WCDR <- pnorm( (qnorm(PD) + sqrt(rho) * qnorm(alpha)) / sqrt(1-rho) )

CreditVaR = L * LGD * (WCDR - PD)

print(CreditVaR)

## [1] 139047



N <- 10000 #Number of iterations

#sim_CreditVaR <- function(Num, Size, LGD, PD, rho, alpha){
  
  iter_loss <- array(0, dim=c(N)) # Distribution of losses per iteration
  
  for(iter in 1:N){
    
    F <- matrix(rnorm(1), 1, Num) # One F Factor value per iteration (same number is needed)
    
    Z <- matrix(rnorm(Num, mean=0, sd=1), 1, Num) # idiosyncratic errors for all loans
    
    U <- sqrt(rho) * F + sqrt(1-rho) * Z # Generate the U_i for all loans
    
    Default <- (U < qnorm(PD)) # Every loan, every iteration, did it default (binary operator)
    
    loan_loss <- Size * Default * LGD # Total loss on each loan for this iteration
    
    iter_loss[iter] <- sum(loan_loss)
    
  }
  
  hist(iter_loss)
  
  EL <- mean(iter_loss)
  
  VaR <- quantile(iter_loss, alpha)
  
  answer <- c(EL, VaR)
  

  ###########
  ############
  indexer <- function(tempU, dat){
    signalAAA <- (tempU[1, ] > array(dat[2, 3], Num))
    tempU <- rbind(tempU, signalAAA)
    signalAA <- ( tempU[1, ] <= array(dat[2, 3], Num) & tempU[1, ] > array(dat[3, 3], Num)) 
    tempU <- rbind(tempU, signalAA)
    signalA <- ( tempU[1, ] <= array(dat[3, 3], Num) & tempU[1, ] > array(dat[4, 3], Num)) 
    tempU <- rbind(tempU, signalA)
    signalBBB <- ( tempU[1, ] <= array(dat[4, 3], Num) & tempU[1, ] > array(dat[5, 3], Num))
    tempU <- rbind(tempU, signalBBB)
    signalBB <- ( tempU[1, ] <= array(dat[5, 3], Num) & tempU[1, ] > array(dat[6, 3], Num)) 
    tempU <- rbind(tempU, signalBB)
    signalB <- ( tempU[1, ] <= array(dat[6, 3], Num) & tempU[1, ] > array(dat[7, 3], Num)) 
    tempU <- rbind(tempU, signalB)
    signalCCC <- ( tempU[1, ] <= array(dat[7, 3], Num) & tempU[1, ] > array(dat[8, 3], Num)) 
    tempU <- rbind(tempU, signalCCC)
    signalD <- ( tempU[1, ] <= array(dat[8, 3], Num))
    tempU <- rbind(tempU, signalD)
    rownames(tempU) <- c("NU", "AAA", "AA", "A", "BBB", "BB", "B", "CCC", "Default")
    subtempU <- tempU[2:nrow(tempU), ]
    next_rate <- apply(subtempU, 1, sum)
    return(next_rate)
  }
  
  func <- function(next_rate, spread, LGD){
   out <-  Size * ( - next_rate[["AAA"]] * (spread[4] - spread[1])/100 * 1/(1 + (spread[1]+0.50)/100) - next_rate[["AA"]] * (spread[4] - spread[2])/100 * 1/(1 + (spread[2]+0.50)/100) - next_rate[["A"]] * (spread[4] - spread[3])/100 * 1/(1 + (spread[3]+0.50)/100) - next_rate[["BB"]] * (spread[4] - spread[5])/100 * 1/(1 + (spread[5]+0.50)/100) - next_rate[["B"]] * (spread[4] - spread[6])/100 * 1/(1 + (spread[6]+0.50)/100)- next_rate[["CCC"]] * (spread[4] - spread[7])/100 * 1/(1 + (spread[7]+0.50)/100)) + Size * next_rate[["CCC"]] * LGD 
    return(out)
   }
  
  N <- 1000 #Number of itera ons should be decreased in order to reduce the computa on  me 
  spread <- c(0.70, 0.88, 1.19, 2.10, 3.39, 4.56, 8.17, 0)
  transition <- c(0.05, 0.19, 4.79, 89.41, 4.35, 0.82, 0.2, 0.19)
  trans_CreditVaR <- function(Num, Size, LGD, PD, rho, alpha, spread, transiton){ 
  dat <- cbind( spread, transition, rev(cumsum(rev(transition)))/100 ) 
  colnames(dat)[3] <- "cum"
  iter_loss <- array(0, dim=c(N)) # Distribu on of losses per itera on
  for(iter in 1:N){
    F <- matrix(rnorm(1), 1, Num) # One F Factor value per itera on (same number is needed) 
    Z <- matrix(rnorm(Num, mean=0, sd=1), 1, Num) # idiosyncra c errors for all loans
    U <- sqrt(rho) * F + sqrt(1-rho) * Z # Generate the U_i for all loans
    Default <- (U < qnorm(PD)) # Every loan, every itera on, did it default (binary operator) 
    loan_loss <- Size * Default * LGD 
    tempU <- pnorm(U)
    tempU <- as.data.frame(tempU)
    next_rate <- indexer(tempU, dat)
    #sum of no onal loan * intrest rate change * df(from 1 year to 2 year)
    iter_loss[iter] <- func(next_rate, spread, LGD)
    #iter_loss[iter] <- 
    }
  hist(iter_loss) 
  EL <- mean(iter_loss)
  VaR <- quantile(iter_loss, alpha)
  answer <- c(EL, VaR)
  return(answer) 
  }
  trans_CreditVaR(Num, Size, LGD, PD, rho, alpha, spread, transition)