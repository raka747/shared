function [loss] = cal_loss(inputs)
    clc;
    
    lambda = inputs(1); 
    mu = inputs(2);
    
    load('database.mat')

    ari = (tri(2:end,:) - tri(1:(end-1), :)) ./ tri(1:(end-1), :); 

    % Taking first day return as zero
    ari = [zeros(1, size(ari,2)); ari]; 
    
    % 246 is the start of the new year. 
    
    dates = zeros(length(myday),3); 
    for i=1:length(myday)
        dates(i,1) = year(myday(i,:)); 
        dates(i,2) = month(myday(i,:)); 
        dates(i,3) = day(myday(i,:)); 
    end

    
    alpharec = zeros(size(ari)); 
    % Every day calculate alphas. analyst reco
    weights_reco = repmat(fliplr(0:44)', 1, size(ari,2)); 
    weights_reco = -(1/1035) .* weights_reco; 
    weights_reco = weights_reco + (1/23); 
    
    for i = 246: size(dates,1)
        past_ret = ari(i-44:i,:);
        weight_ret = past_ret .* weights_reco; 
        alpharec(i,:) = sum(weight_ret); 
        alpharec(i,:) = winsorize(alpharec(i,:)); 
    end
    
    alpharev = zeros(size(ari)); 
    weights_rev = repmat(fliplr(0:20)', 1, size(ari,2)); 
    weights_rev = -(1/231) .* weights_rev; 
    weights_rev = weights_rev + (1/11); 
    R = get_industry_list(allstocks); 
    R_term = (R/(R'*R))*((R)'); 
    I_term = eye(size(R_term)); 
    for i = 246: size(dates,1)
        past_ret = ari(i-20:i,:);
        weight_ret = past_ret .* weights_rev; 
        alpharev(i,:) = -sum(weight_ret); 
        
        % The next computation involves all the alphas. So removing nans 
        % otherwise all these become NaNs
        temp_alpharev = alpharev(i,:); 
        temp_alpharev(isnan(temp_alpharev)) = nanmean(temp_alpharev); 
        alpharev(i,:) = temp_alpharev; 
        alpharev(i,:) = alpharev(i,:) *(I_term - R_term); 
        alpharev(i,:) = winsorize(alpharev(i,:)); 
    end
    
    alphamom = zeros(size(ari)); 
    % Every day calculate alphas. mom
    for i=246:size(dates,1)
        elevenmonret = ari(max(i-252,2):i-22,:);
        elevenmonret = 1+elevenmonret;
        total_ret = cumprod(elevenmonret); 
        total_ret = total_ret - 1; 
        last_ret = total_ret(end,:); 
        alphamom(i,:) = winsorize(last_ret); 
    end


    alphaval = zeros(size(ari)); 
    % Every day calculate alphas. val 
    for i = 246:size(dates,1)
        alphaval(i,:) = winsorize(mtbv(i,:)); 
    end


    % Winsorize even the blended alpha
    alphablend = (1/2)*alpharev + (1/4)* alpharec +  (15/100) * alphaval + (1/10) * alphamom; 
    for i=2:size(dates,1)
        alphablend(i,:) = winsorize(alphablend(i,:)); 
    end




    shrink = zeros(60,1); 

    cur_mon_variance = []; 
    j = 1 ; 
    this_mon_active = []; 

    % book size contains book value for end of today. Returns will start
    % accumulating from the next day
    booksize = zeros(size(ari));

    for i=2:size(dates,1)
        cyear = dates(i,1);
        cmonth = dates(i,2); 
        cday = dates(i,3);

        lyear = dates(i-1,1); 
        lmonth = dates(i-1,2); 
        lday = dates(i-1,3); 


        % Calculate shrink every month using last year returns  
        if cyear >= 1998

            % If the current month is not same as last month
            % This part will get executed each month
            if cmonth ~= lmonth
                lydata = getlastyeardata(cyear, cmonth, cday, dates, ari, isactivenow); 

                this_mon_active = (isactivenow(i,:)>0);
                % Not sure if this is correct making NaNs as zero; 
                lydata(isnan(lydata)) = 0;

                ind_variance = sample_variance(lydata); 
                sigma_bar = mean(ind_variance).* eye(size(lydata,2)); 

                S = zeros(size(lydata,2));
                for k=1:size(lydata,1)
                   % for each day of the last year 
                    X_t = lydata(k,:); 
                    S = S + X_t' * X_t; 
                end
                S = S .* (1/(size(lydata,1))); 

                % Now calculate omega square
                omega_sq = 0; 
                for k = 1:size(lydata,1)
                    X_t = lydata(k,:); 
                    omega_sq = omega_sq + (squared_distance((X_t' * X_t) , S)); 
                end    
                T_len = size(lydata,1);
                omega_sq = omega_sq .* (1/(T_len*(T_len-1))); 

                delta_sq = (squared_distance(S , sigma_bar)) - omega_sq; 

                shrink(j) = delta_sq/(delta_sq + omega_sq); 

                cur_mon_variance = (1 - shrink(j)) * sigma_bar + (shrink(j))* S; 
                j = j+1;
            end

             
            sigma = cur_mon_variance; 

            alpha = alphablend(i, this_mon_active); 
            % For those stocks, for which alpha is missing, taking mean. 
            temp = nanmean(alpha);
            temp(isnan(temp)) = nanmean(temp); % if there does not even nanmean
            alpha(isnan(alpha)) =  temp; 
            tau = tcost(i, this_mon_active);
            % For those stocks, for which tcost is missing, taking mean. 
            temp = nanmean(tau);
            temp(isnan(temp)) = nanmean(temp); 
            tau(isnan(tau)) = temp; 
            
            % Theta - max trade size
            theta = 15* (10^6); 
            
            % Pi - max position size
            pi = 50*(10^6); 
            prev_wts= booksize(i,this_mon_active); 

            [dportwts,~] = cal_desired_wts(lambda, mu, sigma, alpha', tau', theta, pi, prev_wts');

            X = prev_wts' + dportwts(1:size(dportwts,1)/2, :) - dportwts((size(dportwts,1)/2+1):end, :);
            booksize(i+1,this_mon_active) = X; 
        end    
    end

    trade = zeros(size(ari));
    total_days = 0;
    total_trade = 0; 
    
    % we started optimising from the start of the year. So book building
    % starts from the second tradig day which is 247
    for i=247:size(trade,1)
        trade(i,:) = booksize(i,:) - booksize(i-1,:);  
        total_trade = total_trade+ sum(abs(trade(i,:)));  
        total_days = total_days + 1; 
    end
    avg_trade_day = total_trade / total_days ; 

    total_days = 0;
    total_long_book = 0; 
    total_short_book = 0; 
    for i=247:size(trade,1)
        total_long_book = total_long_book + sum(booksize(i, booksize(i,:) > 0));
        total_short_book = total_short_book - sum(booksize(i, booksize(i,:) < 0));
        total_days = total_days + 1; 
    end

    avg_long_book = total_long_book/total_days; 
    avg_short_book = total_short_book/total_days;
    
    loss = 0; 
%     loss = loss + (avg_trade_day - 15*(10^6))^2;
%     loss = loss + (avg_long_book - 50*(10^6))^2; 
%     loss = loss + (avg_short_book - 50*(10^6))^2; 

    % backweightcalculation
    % todays back_weight = yp(1+r) + today trade; 
    back_weight = zeros(size(ari)); 
    for i=247:size(ari,1)
        back_weight(i,:) = booksize(i-1,:).*(1+ari(i,:)) + trade(i,:); 
    end
    
    % computing profit per day 
    % profit = yesterday booksize * today returns - today trade size *
    % tcost
    
    for i=1:size(tcost,2)
        tcost(isnan(tcost(:,i)), i) = nanmean(tcost(:,i));
    end
    
    tcost(isnan(tcost)) = nanmean(nanmean(tcost));
    ari(isnan(ari)) = 0 ; 
    
    pnl_ind = zeros(size(ari)); 
    for i = 247:size(ari,1)
        pnl_ind(i,:) = booksize(i-1,:) .* ari(i,:) - abs(trade(i,:)) .* tcost(i,:); 
    end
    
    pnl = cumsum(sum(pnl_ind,2)); 
    
    % calculating returns using absolute book value of yesterday. 
    returns = zeros(size(pnl)); 
    for i = 248:size(pnl,1)
        returns(i) = (pnl(i) - pnl(i-1)) / sum(abs(booksize(i-1,:))) ; 
    end
    
    highwatermark = zeros(size(pnl));
    drawdown = zeros(size(pnl));
    duration = zeros(size(pnl)); 
    for i=246:size(pnl,1)
        cur_hwm = max(highwatermark(i-1,1), pnl(i,1));
        highwatermark(i,1) = cur_hwm; 
            drawdown(i,1) = (cur_hwm - pnl(i,1)); 
        if drawdown(i,1) == 0 
            duration(i,1) = 0 ; 
        else
            duration(i,1) = duration(i-1,1) + 1; 
        end    
    end    
        
    deepest_dd = max(drawdown); 
    longest_dd = max(duration); 
    sharpe = (mean(returns)/std(returns))* sqrt(252); 
    tradesize = sum(trade,2); 
    booksize = sum(booksize,2); 
    booksize = booksize(1:end-1,:); 
    t0 = 247; 
    save('result.mat', 'shrink', 'alpharev', 'alpharec', 'alphaval', 'alphamom', 'alphablend', 'lambda', 'mu', 't0', 'trade', 'back_weight','pnl', 'booksize', 'tradesize', 'sharpe', 'longest_dd', 'deepest_dd' );
    
    y = 3; 
end
