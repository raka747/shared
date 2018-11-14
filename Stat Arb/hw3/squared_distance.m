function [dist] = squared_distance(A,B)
    A_B = A - B ; 
    A_B_2 = A_B .^2 ; 
    dist = sum(A_B_2(:)); 
end