function[lydata] = getlastyeardata(cyear, cmonth,cday, totaldates, data, isactivenow)
% cyear - current year
% cmonth - current month
% cday - current day
% totaldates - dates matrix
% data - The data to be indexed for the last year
% isactivenow - whether the stocks are active that month or not

% lydata - The data for the last year

lyearindex = -1; 
lyear = cyear -1; 
    for day=1:30
        target = [lyear, cmonth, day]; 
        [datepresent, daterow] = ismember(target, totaldates, 'rows'); 
        if datepresent
            lyearindex = daterow;
            break;
        end
    end

target = [cyear, cmonth, cday]; 
[~, cyearindex] = ismember(target, totaldates, 'rows'); 

activethismonth = (isactivenow(cyearindex,:)>0); 
lydata = data(lyearindex:(cyearindex-1),activethismonth); 
end