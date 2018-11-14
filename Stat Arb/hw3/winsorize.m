function [result] = winsorize(returns)
    demean_returns = returns - nanmean(returns); 
    std_returns = demean_returns ./ nanstd(returns); 
    
    % if alpha > 3 make it 3. if it is less than -3 make it -3. 
    std_returns(std_returns > 3) = 3; 
    std_returns(std_returns < -3) = -3; 
    
    result = std_returns; 
end