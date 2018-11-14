function [sample_var] = sample_variance(data)
    % data is of form T X N . 
    
    % Have to be careful as some cols are entire NaNs
    square_data = data .^2 ; 
    sum_sq_data = sum(square_data, 1); 
    factor = 1/(size(data,1)-1);
    sample_var = sum_sq_data .* factor; 
end