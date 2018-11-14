function [dportwts,exitflag] = cal_desired_wts(lambda, mu, sigma, alpha, tau, theta, pi, prev_wts)
    
    % lambda - scalar turnover controller
    % mu - scalar booksize controller
    % sigma - covariance of month begining activestocks
    % alpha - alpha of month begining activestocks
    % tau - tcosts of month begin activestocks
    % theta - theta trade size scalar
    % pi - position size scalar
    % prev_wts - prev day wts of the activestocks



    H = 2* mu * [sigma, -sigma; -sigma sigma];
    g = [2*mu* sigma* prev_wts - alpha + lambda*tau; -2*mu*sigma*prev_wts + alpha + lambda*tau];
    
    LB = zeros(size(g)); 
    
    UB1_size = size(LB,1)/2; 
    theta_mat = theta* ones(UB1_size,1);
    pi_mat = pi * ones(UB1_size,1);
    zero_mat = zeros(size(pi_mat));
    UB1 = max(zero_mat, min(theta_mat, pi_mat-prev_wts)); 
    UB2 = max(zero_mat, min(theta_mat, pi_mat+prev_wts)); 
    UB = [UB1; UB2]; 
    
    options = optimset('Algorithm', 'interior-point-convex');
%     options = optimset(options, 'Display', 'iter');
    [dportwts,~,exitflag,~] = quadprog(H,g,[],[],[],[],LB,UB,[], options);
end