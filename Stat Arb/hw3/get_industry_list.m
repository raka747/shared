function [R, indlist] = get_industry_list(allstocks)
    total_industries = {}; 
    for i=1:size(allstocks,2)
        total_industries = [total_industries, allstocks(i).industrylist.industry]; 
    end
    [indlist, ~, iR] = unique(total_industries); 
    
    R = zeros(size(allstocks,2), length(indlist)); 
    for i=1:size(allstocks,2)
        R(i,iR(i)) = 1; 
    end

end