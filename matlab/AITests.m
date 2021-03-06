x0 = [562;53];
x1 = [65;80];
a = [0;-30];

flightTime = 5;
targetPosError = [90;90];

t=linspace(0,flightTime,100);

% v_0(t_f) = (x1-x0-1/2at^2)/t
v0_fkt = @(t_f,a,x0,x1) (x1-x0-a.*(repmat(t_f,2,1).^2)./2)./repmat(t_f,2,1);

% x(t)= 1/2*a*t^2+v_0*t+x_0
x=@(t,a,x0,v0) repmat(a,1,length(t)).*(repmat(t,2,1).^2)./2 + repmat(v0,1,length(t)).*repmat(t,2,1) + repmat(x0,1,length(t));

v0 = v0_fkt(flightTime,a,x0,x1);

xs = x(t,a,x0,v0);

hold on;
cla;
axis equal;
scatter(x0(1),x0(2));
scatter(x1(1),x1(2));

plot(xs(1,:),xs(2,:));

for i=1:4
    v0 = v0_fkt(flightTime,a,x0,x1-0.5*targetPosError + targetPosError.*rand(2,1));
    xs = x(t,a,x0,v0);
    plot(xs(1,:),xs(2,:));
end