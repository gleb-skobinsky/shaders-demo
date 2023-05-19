import org.intellij.lang.annotations.Language

@Language("GLSL")
const val compositeSksl = """
                uniform float2 iResolution;      // Viewport resolution (pixels)
                uniform float  iTime;            // Shader playback time (s)
                
                const float cloudscale = 1.1;
                const float speed = 0.03;
                const float clouddark = 0.5;
                const float cloudlight = 0.3;
                const float cloudcover = 0.2;
                const float cloudalpha = 8.0;
                const float skytint = 0.5;
                const vec3 skycolour1 = vec3(0.2, 0.4, 0.6);
                const vec3 skycolour2 = vec3(0.4, 0.7, 1.0);

                const mat2 m = mat2( 1.6,  1.2, -1.2,  1.6 );

                vec2 hash( vec2 p ) {
                	p = vec2(dot(p,vec2(127.1,311.7)), dot(p,vec2(269.5,183.3)));
                	return -1.0 + 2.0*fract(sin(p)*43758.5453123);
                }

                float noise( in vec2 p ) {
                    const float K1 = 0.366025404; // (sqrt(3)-1)/2;
                    const float K2 = 0.211324865; // (3-sqrt(3))/6;
                	vec2 i = floor(p + (p.x+p.y)*K1);	
                    vec2 a = p - i + (i.x+i.y)*K2;
                    vec2 o = (a.x>a.y) ? vec2(1.0,0.0) : vec2(0.0,1.0); //vec2 of = 0.5 + 0.5*vec2(sign(a.x-a.y), sign(a.y-a.x));
                    vec2 b = a - o + K2;
                	vec2 c = a - 1.0 + 2.0*K2;
                    vec3 h = max(0.5-vec3(dot(a,a), dot(b,b), dot(c,c) ), 0.0 );
                	vec3 n = h*h*h*h*vec3( dot(a,hash(i+0.0)), dot(b,hash(i+o)), dot(c,hash(i+1.0)));
                    return dot(n, vec3(70.0));	
                }

                float fbm(vec2 n) {
                	float total = 0.0, amplitude = 0.1;
                	for (int i = 0; i < 7; i++) {
                		total += noise(n) * amplitude;
                		n = m * n;
                		amplitude *= 0.4;
                	}
                	return total;
                }

                // -----------------------------------------------

                vec4 main(in vec2 fragCoord) {
                    vec2 p = fragCoord.xy / iResolution.xy;
                	vec2 uv = p*vec2(iResolution.x/iResolution.y,1.0);    
                    float time = iTime * speed;
                    float q = fbm(uv * cloudscale * 0.5);
                    
                    //ridged noise shape
                	float r = 0.0;
                	uv *= cloudscale;
                    uv -= q - time;
                    float weight = 0.8;
                    for (int i=0; i<8; i++){
                		r += abs(weight*noise( uv ));
                        uv = m*uv + time;
                		weight *= 0.7;
                    }
                    
                    //noise shape
                	float f = 0.0;
                    uv = p*vec2(iResolution.x/iResolution.y,1.0);
                	uv *= cloudscale;
                    uv -= q - time;
                    weight = 0.7;
                    for (int i=0; i<8; i++){
                		f += weight*noise( uv );
                        uv = m*uv + time;
                		weight *= 0.6;
                    }
                    
                    f *= r + f;
                    
                    //noise colour
                    float c = 0.0;
                    time = iTime * speed * 2.0;
                    uv = p*vec2(iResolution.x/iResolution.y,1.0);
                	uv *= cloudscale*2.0;
                    uv -= q - time;
                    weight = 0.4;
                    for (int i=0; i<7; i++){
                		c += weight*noise( uv );
                        uv = m*uv + time;
                		weight *= 0.6;
                    }
                    
                    //noise ridge colour
                    float c1 = 0.0;
                    time = iTime * speed * 3.0;
                    uv = p*vec2(iResolution.x/iResolution.y,1.0);
                	uv *= cloudscale*3.0;
                    uv -= q - time;
                    weight = 0.4;
                    for (int i=0; i<7; i++){
                		c1 += abs(weight*noise( uv ));
                        uv = m*uv + time;
                		weight *= 0.6;
                    }
                	
                    c += c1;
                    
                    vec3 skycolour = mix(skycolour2, skycolour1, p.y);
                    vec3 cloudcolour = vec3(1.1, 1.1, 0.9) * clamp((clouddark + cloudlight*c), 0.0, 1.0);
                   
                    f = cloudcover + cloudalpha*f*r;
                    
                    vec3 result = mix(skycolour, clamp(skytint * skycolour + cloudcolour, 0.0, 1.0), clamp(f + c, 0.0, 1.0));
                    
                	return vec4( result, 1.0 );
                }
            """

@Language("GLSL")
const val sksl2 = """
    uniform float2 iResolution;      // Viewport resolution (pixels)
    uniform float  iTime;            // Shader playback time (s)
        float sun(vec2 uv, float battery)
    {
        float val = smoothstep(0.3, 0.29, length(uv));
        float bloom = smoothstep(0.7, 0.0, length(uv));
        float cut = 3.0 * sin((uv.y + iTime * 0.2 * (battery + 0.02)) * 100.0) 
                    + clamp(uv.y * 14.0 + 1.0, -6.0, 6.0);
        cut = clamp(cut, 0.0, 1.0);
        return clamp(val * cut, 0.0, 1.0) + bloom * 0.6;
    }
    
    float grid(vec2 uv, float battery)
    {
        vec2 size = vec2(uv.y, uv.y * uv.y * 0.2) * 0.01;
        uv += vec2(0.0, iTime * 4.0 * (battery + 0.05));
        uv = abs(fract(uv) - 0.5);
        vec2 lines = smoothstep(size, vec2(0.0), uv);
        lines += smoothstep(size * 5.0, vec2(0.0), uv) * 0.4 * battery;
        return clamp(lines.x + lines.y, 0.0, 3.0);
    }
    
    float dot2(in vec2 v ) { return dot(v,v); }
    
    float sdTrapezoid( in vec2 p, in float r1, float r2, float he )
    {
        vec2 k1 = vec2(r2,he);
        vec2 k2 = vec2(r2-r1,2.0*he);
        p.x = abs(p.x);
        vec2 ca = vec2(p.x-min(p.x,(p.y<0.0)?r1:r2), abs(p.y)-he);
        vec2 cb = p - k1 + k2*clamp( dot(k1-p,k2)/dot2(k2), 0.0, 1.0 );
        float s = (cb.x<0.0 && ca.y<0.0) ? -1.0 : 1.0;
        return s*sqrt( min(dot2(ca),dot2(cb)) );
    }
    
    float sdLine( in vec2 p, in vec2 a, in vec2 b )
    {
        vec2 pa = p-a, ba = b-a;
        float h = clamp( dot(pa,ba)/dot(ba,ba), 0.0, 1.0 );
        return length( pa - ba*h );
    }
    
    float sdBox( in vec2 p, in vec2 b )
    {
        vec2 d = abs(p)-b;
        return length(max(d,vec2(0))) + min(max(d.x,d.y),0.0);
    }
    
    float opSmoothUnion(float d1, float d2, float k){
        float h = clamp(0.5 + 0.5 * (d2 - d1) /k,0.0,1.0);
        return mix(d2, d1 , h) - k * h * ( 1.0 - h);
    }
    
    float sdCloud(in vec2 p, in vec2 a1, in vec2 b1, in vec2 a2, in vec2 b2, float w)
    {
        //float lineVal1 = smoothstep(w - 0.0001, w, sdLine(p, a1, b1));
        float lineVal1 = sdLine(p, a1, b1);
        float lineVal2 = sdLine(p, a2, b2);
        vec2 ww = vec2(w*1.5, 0.0);
        vec2 left = max(a1 + ww, a2 + ww);
        vec2 right = min(b1 - ww, b2 - ww);
        vec2 boxCenter = (left + right) * 0.5;
        //float boxW = right.x - left.x;
        float boxH = abs(a2.y - a1.y) * 0.5;
        //float boxVal = sdBox(p - boxCenter, vec2(boxW, boxH)) + w;
        float boxVal = sdBox(p - boxCenter, vec2(0.04, boxH)) + w;
        
        float uniVal1 = opSmoothUnion(lineVal1, boxVal, 0.05);
        float uniVal2 = opSmoothUnion(lineVal2, boxVal, 0.05);
        
        return min(uniVal1, uniVal2);
    }
    
    vec4 main(in vec2 fragCoord )
    {
        vec2 uv = (2.0 * fragCoord.xy - iResolution.xy)/iResolution.y;
        float battery = 1.0;
        //if (iMouse.x > 1.0 && iMouse.y > 1.0) battery = iMouse.y / iResolution.y;
        //else battery = 0.8;
        
        //if (abs(uv.x) < (9.0 / 16.0))
        {
            // Grid
            float fog = smoothstep(0.1, -0.02, abs(uv.y + 0.2));
            vec3 col = vec3(0.0, 0.1, 0.2);
            if (uv.y < -0.2)
            {
                uv.y = 3.0 / (abs(uv.y + 0.2) + 0.05);
                uv.x *= uv.y * 1.0;
                float gridVal = grid(uv, battery);
                col = mix(col, vec3(1.0, 0.5, 1.0), gridVal);
            }
            else
            {
                float fujiD = min(uv.y * 4.5 - 0.5, 1.0);
                uv.y -= battery * 1.1 - 0.51;
                
                vec2 sunUV = uv;
                vec2 fujiUV = uv;
                
                // Sun
                sunUV += vec2(0.75, 0.2);
                //uv.y -= 1.1 - 0.51;
                col = vec3(1.0, 0.2, 1.0);
                float sunVal = sun(sunUV, battery);
                
                col = mix(col, vec3(1.0, 0.4, 0.1), sunUV.y * 2.0 + 0.2);
                col = mix(vec3(0.0, 0.0, 0.0), col, sunVal);
                
                // fuji
                float fujiVal = sdTrapezoid( uv  + vec2(-0.75+sunUV.y * 0.0, 0.5), 1.75 + pow(uv.y * uv.y, 2.1), 0.2, 0.5);
                float waveVal = uv.y + sin(uv.x * 20.0 + iTime * 2.0) * 0.05 + 0.2;
                float wave_width = smoothstep(0.0,0.01,(waveVal));
                
                // fuji color
                col = mix( col, mix(vec3(0.0, 0.0, 0.25), vec3(1.0, 0.0, 0.5), fujiD), step(fujiVal, 0.0));
                // fuji top snow
                col = mix( col, vec3(1.0, 0.5, 1.0), wave_width * step(fujiVal, 0.0));
                // fuji outline
                col = mix( col, vec3(1.0, 0.5, 1.0), 1.0-smoothstep(0.0,0.01,abs(fujiVal)) );
                //col = mix( col, vec3(1.0, 1.0, 1.0), 1.0-smoothstep(0.03,0.04,abs(fujiVal)) );
                //col = vec3(1.0, 1.0, 1.0) *(1.0-smoothstep(0.03,0.04,abs(fujiVal)));
                
                // horizon color
                col += mix( col, mix(vec3(1.0, 0.12, 0.8), vec3(0.0, 0.0, 0.2), clamp(uv.y * 3.5 + 3.0, 0.0, 1.0)), step(0.0, fujiVal) );
                
                // cloud
                vec2 cloudUV = uv;
                cloudUV.x = mod(cloudUV.x + iTime * 0.1, 4.0) - 2.0;
                float cloudTime = iTime * 0.5;
                float cloudY = -0.5;
                float cloudVal1 = sdCloud(cloudUV, 
                                         vec2(0.1 + sin(cloudTime + 140.5)*0.1,cloudY), 
                                         vec2(1.05 + cos(cloudTime * 0.9 - 36.56) * 0.1, cloudY), 
                                         vec2(0.2 + cos(cloudTime * 0.867 + 387.165) * 0.1,0.25+cloudY), 
                                         vec2(0.5 + cos(cloudTime * 0.9675 - 15.162) * 0.09, 0.25+cloudY), 0.075);
                cloudY = -0.6;
                float cloudVal2 = sdCloud(cloudUV, 
                                         vec2(-0.9 + cos(cloudTime * 1.02 + 541.75) * 0.1,cloudY), 
                                         vec2(-0.5 + sin(cloudTime * 0.9 - 316.56) * 0.1, cloudY), 
                                         vec2(-1.5 + cos(cloudTime * 0.867 + 37.165) * 0.1,0.25+cloudY), 
                                         vec2(-0.6 + sin(cloudTime * 0.9675 + 665.162) * 0.09, 0.25+cloudY), 0.075);
                
                float cloudVal = min(cloudVal1, cloudVal2);
                
                //col = mix(col, vec3(1.0,1.0,0.0), smoothstep(0.0751, 0.075, cloudVal));
                col = mix(col, vec3(0.0, 0.0, 0.2), 1.0 - smoothstep(0.075 - 0.0001, 0.075, cloudVal));
                col += vec3(1.0, 1.0, 1.0)*(1.0 - smoothstep(0.0,0.01,abs(cloudVal - 0.075)));
            }
    
            col += fog * fog * fog;
            col = mix(vec3(col.r, col.r, col.r) * 0.5, col, battery * 0.7);

        return vec4(col,1.0);
    }
    }
    //else fragColor = vec4(0.0);
"""