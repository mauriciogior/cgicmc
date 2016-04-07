#version 150

in vec3 v_position;

out vec4 fragColor;

#define MIN_ENERGY 0.001f
#define MAX_STEPS 5
#define EPSILON 0.001f
#define POSITIVE_INFINITY 10e10

struct CAMERA {
  vec3 position;
  vec3 look_at;
  vec3 view_up;
  float fov;
};

struct RAY {
  vec3 origin;
  vec3 direction;
  float energy;
};

struct MATERIAL {
  vec3 ka;
  vec3 kd;
  vec3 ks;
  float ns;
  float reflt;
};

struct LIGHT {
  vec3 position;
  vec3 la;
  vec3 ld;
  vec3 ls;
};

struct SPHERE {
  vec3 center;
  float radius;
  MATERIAL material;
};

struct BOX {
  vec3 center;
  float sx;
  float sy;
  float sz;
  MATERIAL material;
};

struct SCENE {
  vec3 background;
  int nr_spheres;
  int nr_lights;
  int nr_boxes;
  SPHERE spheres[9];
  BOX boxes[1];
  LIGHT lights[1];
  CAMERA camera;
};

struct INTERSECTION {
  MATERIAL material;
  vec3 position;
  vec3 normal;
  float dist;
};

float intersect_sphere(SPHERE sphere, RAY ray) {
  vec3 v = ray.origin - sphere.center;
  float b = 2.0f * dot(ray.direction, v);
  float c = dot(v,v) - sphere.radius * sphere.radius;
  float d = b * b - 4.0f * c;
  if(d > 0.0f) {
    float x1 = (-b - sqrt(d)) / 2.0f;
    float x2 = (-b + sqrt(d)) / 2.0f;
    if(x1 >= 0.0f && x2 >= 0.0f) return x1;
    if(x1 < 0.0f && x2 >= 0.0f) return x2;
  }
 
  return -1.0f; //no intersection
}

float intersect_box(BOX box, RAY ray) {
  float xhit, yhit, zhit;
  float ss = POSITIVE_INFINITY; //final distance

  //translate the ray origin so that the object center is (0,0,0)
  float xadj = ray.origin.x - box.center.x;
  float yadj = ray.origin.y - box.center.y;
  float zadj = ray.origin.z - box.center.z;

    //check the x faces
  if (ray.direction.x != 0.0f) {
    float s = (box.sx / 2.0f - xadj) / ray.direction.x;
    if (s > 0.0f && s < ss) {
      yhit = abs(yadj + s * ray.direction.y);
      zhit = abs(zadj + s * ray.direction.z);

      if ((yhit < box.sy / 2.0f) && (zhit < box.sz / 2)) {
        ss = s;
      }
    }

    s = (-box.sx / 2.0f - xadj) / ray.direction.x;
    if ((s > 0.0f) && (s < ss)) {
      yhit = abs(yadj + s * ray.direction.y);
      zhit = abs(zadj + s * ray.direction.z);
      if ((yhit < box.sy / 2.0f) && (zhit < box.sz / 2.0f)) {
        ss = s;
      }
    }
  }

  //check the y faces
  if (ray.direction.y != 0.0f) {
    float s = (box.sy / 2.0f - yadj) / ray.direction.y;
    if (s > 0.0f && s < ss) {
      xhit = abs(xadj + s * ray.direction.x);
      zhit = abs(zadj + s * ray.direction.z);

      if ((xhit < box.sx / 2.0f) && (zhit < box.sz / 2.0f)) {
        ss = s;
      }
    }

    s = (-box.sy / 2.0f - yadj) / ray.direction.y;
    if ((s > 0.0f) && (s < ss)) {
      xhit = abs(xadj + s * ray.direction.x);
      zhit = abs(zadj + s * ray.direction.z);
      if ((xhit < box.sx / 2.0f) && (zhit < box.sz / 2.0f)) {
        ss = s;
      }
    }
  }

  //check the z faces
  if (ray.direction.z != 0.0f) {
    float s = (box.sz / 2.0f - zadj) / ray.direction.z;
    if (s > 0 && s < ss) {
      xhit = abs(xadj + s * ray.direction.x);
      yhit = abs(yadj + s * ray.direction.y);

      if ((xhit < box.sx / 2.0f) && (yhit < box.sy / 2.0f)) {
        ss = s;
      }
    }

    s = (-box.sz / 2.0f - zadj) / ray.direction.z;
    if ((s > 0.0f) && (s < ss)) {
      xhit = abs(xadj + s * ray.direction.x);
      yhit = abs(yadj + s * ray.direction.y);
      if ((xhit < box.sx / 2.0f) && (yhit < box.sy / 2.0f)) {
        ss = s;
      }
    }
  }

  if (ss == POSITIVE_INFINITY) {
    return -1.0f;
  }

  return ss;
}

vec3 normal_sphere(SPHERE sphere, vec3 intersection) {
  return normalize(intersection-sphere.center);
}

vec3 normal_box(BOX box, vec3 intersection) {
  int face = 0;
  float diff, ss = POSITIVE_INFINITY;

  diff = abs((box.center.x + box.sx / 2.0f) - intersection.x);
  if (ss > diff) {
    ss = diff;
    face = 0;
  }

  diff = abs((box.center.x - box.sx / 2.0f) - intersection.x);
  if (ss > diff) {
    ss = diff;
    face = 1;
  }

  diff = abs((box.center.y + box.sy / 2.0f) - intersection.y);
  if (ss > diff) {
    ss = diff;
    face = 2;
  }

  diff = abs((box.center.y - box.sy / 2.0f) - intersection.y);
  if (ss > diff) {
    ss = diff;
    face = 3;
  }

  diff = abs((box.center.z + box.sz / 2.0f) - intersection.z);
  if (ss > diff) {
    ss = diff;
    face = 4;
  }

  diff = abs((box.center.z -box.sz / 2.0f) - intersection.z);
  if (ss > diff) {
    ss = diff;
    face = 5;
  }

  switch (face) {
    case 0:
      return vec3(1.0f,0.0f,0.0f);
    case 1:
      return vec3(-1.0f,0.0f,0.0f);
    case 2:
      return vec3(0.0f,1.0f,0.0f);
    case 3:
      return vec3(0.0f,-1.0f,0.0f);
    case 4:
      return vec3(0.0f,0.0f,1.0f);
    case 5:
      return vec3(0.0f,0.0f,-1.0f);
  }

  return vec3(0.0f,0.0f,0.0f);
}

INTERSECTION intersect_scene(SCENE scene, RAY ray) {
  INTERSECTION inter;
  inter.dist = -1.0f;

  float mindist = POSITIVE_INFINITY; //positive infinity

  for (int i = 0; i < scene.nr_spheres; i++) {
    float dist = intersect_sphere(scene.spheres[i], ray);

    if (dist >= 0.0f && mindist > dist) {
      mindist = dist;
      inter.material = scene.spheres[i].material;
      inter.position = ray.origin + dist * ray.direction;
      inter.dist = dist;
      inter.normal = normal_sphere(scene.spheres[i], inter.position); 
    }
  }

  for (int i = 0; i < scene.nr_boxes; i++) {
    float dist = intersect_box(scene.boxes[i], ray);

    if (dist >= 0.0f && mindist > dist) {
      mindist = dist;
      inter.material = scene.boxes[i].material;
      inter.position = ray.origin + dist * ray.direction;
      inter.dist = dist;
      inter.normal = normal_box(scene.boxes[i], inter.position); 
    }
  }  

  //moving the intersection point on the direction of the normal
  //a small fraction in order to avoid problems related to aritmetic
  //approximation: due to the approximation, the intersection point
  //may be placed inside the object surface, so the next intersection
  //will be the same surface point!
  inter.position  = inter.position  + EPSILON * inter.normal;

  return inter;
}

vec3 phong(MATERIAL material, SCENE scene, vec3 N, vec3 intersection) {
  vec3 color = vec3(0.0f,0.0f,0.0f); //final color

  //for each light
  for (int i = 0; i < scene.nr_lights; i++) {
    //calculate the light direction
    LIGHT light = scene.lights[i];
    vec3 L = normalize(light.position-intersection);

    //adding the ambient contribution
    color += material.ka * light.la;

    float diff = dot(N,L);
    if (diff > 0.0f) {
     //calculating the difusse component
     color += material.kd * light.ld * diff;

      //calculating the specular component
      vec3 R = normalize(reflect(-L,N));
      float spec = dot(R,L);
      if (spec > 0.0f) {
        spec = max(0.0f, pow(spec, material.ns));
        color += material.ks * light.ls * spec;
      }
    }
  }
  return color;
}

vec3 phong_shadow(MATERIAL material, SCENE scene, vec3 N, vec3 intersection) {
  vec3 color = vec3(0.0f,0.0f,0.0f); //final color

  //for each light
  for (int i = 0; i < scene.nr_lights; i++) {
    LIGHT light = scene.lights[i];

    //adding the ambient contribution
    color += material.ka * light.la;    

    //shadow ray
    RAY shadowray;
    shadowray.origin = intersection;
    shadowray.direction = normalize(vec3(light.position-intersection));
    INTERSECTION intersect = intersect_scene(scene, shadowray);

    //there is not an object between the light and the object
    if (intersect.dist == -1) {
      //calculate the light direction
      vec3 L = normalize(light.position-intersection);

      float diff = dot(N,L);
      if (diff > 0.0f) {
        //calculating the difusse component
        color += material.kd * light.ld * diff;

        //calculating the specular component
        vec3 R = normalize(reflect(-L,N));
        float spec = dot(R,L);
        if (spec > 0.0f) {
          spec = max(0.0f, pow(spec, material.ns));
          color += material.ks * light.ls * spec;
        }
      }
    }
  }

  return color;
}

RAY shoot_ray(CAMERA camera, vec3 pos) {
  RAY ray;
  ray.energy = 1.0f;

  vec3 n = normalize(camera.position-camera.look_at);
  vec3 u = normalize(cross(camera.view_up, n));
  vec3 v = normalize(cross(n,u));

  float zvp = 1.0f/(tan(radians(camera.fov/2.0f)));

  vec3 point = camera.position + u*pos.x + v*pos.y;
  
  ray.origin = camera.position + zvp*n;
  ray.direction = normalize(point-ray.origin);

  return ray;
}

vec3 ray_casting(SCENE scene, vec3 pos) {
  vec3 color = scene.background; //final color is background color
  RAY ray = shoot_ray(scene.camera, pos);    

  INTERSECTION intersect = intersect_scene(scene, ray); //get the intersection

  if (intersect.dist >= 0.0f) {
    color = phong_shadow(intersect.material, scene, 
                         intersect.normal, 
                         intersect.position);
   }

  //set the color
  return color;
}

vec3 ray_tracing(SCENE scene, vec3 pos) {
  int step = 0;
  vec3 final_color = scene.background; //final color is background color
  
  RAY ray = shoot_ray(scene.camera, pos);

  while (step < MAX_STEPS && ray.energy > MIN_ENERGY) {
    //getting the intersection
    INTERSECTION intersect = intersect_scene(scene, ray);

    if (intersect.dist >= 0.0f) {
      //calculating the phong contribution
      vec3 color =  phong_shadow(intersect.material, scene, 
                                 intersect.normal, 
                                 intersect.position);
      final_color += (ray.energy * color);

      //calculating the transmitted light
      //...

      //calculating the reflected ray
      ray.direction = normalize(reflect(ray.direction, intersect.normal));
      ray.origin = intersect.position;
      ray.energy = ray.energy * intersect.material.reflt; 
    }

    step++;
  }

  //set the color
  return final_color;
}

vec3 ray_tracing_antialiasing(SCENE scene, vec3 pos) {
  vec3 final_color = vec3(0.0f,0.0f,0.0f);

  final_color += ray_tracing(scene, vec3(pos.x, pos.y, pos.z));
  final_color += ray_tracing(scene, vec3(pos.x-0.000625f, pos.y-0.000625f, pos.z));
  final_color += ray_tracing(scene, vec3(pos.x-0.000625f, pos.y+0.000625f, pos.z));
  final_color += ray_tracing(scene, vec3(pos.x+0.000625f, pos.y-0.000625f, pos.z));
  final_color += ray_tracing(scene, vec3(pos.x+0.000625f, pos.y+0.000625f, pos.z));

  final_color += ray_tracing(scene, vec3(pos.x+0.000625f, pos.y, pos.z));
  final_color += ray_tracing(scene, vec3(pos.x-0.000625f, pos.y, pos.z));
  final_color += ray_tracing(scene, vec3(pos.x, pos.y+0.000625f, pos.z));
  final_color += ray_tracing(scene, vec3(pos.x, pos.y-0.000625f, pos.z));

  //set the color
  return final_color/9.0f;
}

SCENE create_scene_1() {
  SCENE scene;
  scene.background = vec3(0.0f,0.0f,0.0f);
  scene.nr_boxes = 0;

  scene.camera.position = vec3(1.0f,0.0f,4.0f);
  scene.camera.view_up = vec3(0.0f,1.0f,0.0f);
  scene.camera.look_at = vec3(0.0f,0.0f,0.0f);
  scene.camera.fov = 45.0f;
 
  scene.nr_lights = 1;
  scene.lights[0].position = vec3(10.0f,10.0f,10.0f);
  scene.lights[0].la = vec3(1.0f,1.0f,1.0f);
  scene.lights[0].ld = vec3(1.0f,1.0f,1.0f);
  scene.lights[0].ls = vec3(1.0f,1.0f,1.0f);

  scene.nr_spheres = 3;
  scene.spheres[0].center = vec3(0.0f,0.0f,0.0f);
  scene.spheres[0].radius = 1.0f;
  scene.spheres[0].material.ka = vec3(0.1f,0.1f,0.1f);
  scene.spheres[0].material.kd = vec3(0.8f,0.08f,0.8f);
  scene.spheres[0].material.ks = vec3(0.98f,0.8f,0.98f);
  scene.spheres[0].material.ns = 128.0f;
  scene.spheres[0].material.reflt = 0.05f;

  scene.spheres[1].center = vec3(1.5f,0.0f,0.5f);
  scene.spheres[1].radius = 0.5f;
  scene.spheres[1].material.ka = vec3(0.1f,0.1f,0.1f);
  scene.spheres[1].material.kd = vec3(0.8f,0.8f,0.08f);
  scene.spheres[1].material.ks = vec3(0.98f,0.98f,0.8f);
  scene.spheres[1].material.ns = 128.0f;
  scene.spheres[1].material.reflt = 0.25f;

  scene.spheres[2].center = vec3(-1.15f,-0.5f,0.5f);
  scene.spheres[2].radius = 0.5f;
  scene.spheres[2].material.ka = vec3(0.1f,0.1f,0.1f);
  scene.spheres[2].material.kd = vec3(0.08f,0.08f,0.8f);
  scene.spheres[2].material.ks = vec3(0.98f,0.98f,0.98f);
  scene.spheres[2].material.ns = 128.0f;
  scene.spheres[2].material.reflt = 0.25f;

  scene.nr_boxes = 1;
  scene.boxes[0].center = vec3(0.0f,-1.1f,0.0f);
  scene.boxes[0].sx = 6.0f;
  scene.boxes[0].sy = 0.1f;
  scene.boxes[0].sz = 6.0f;
  scene.boxes[0].material.ka = vec3(0.1f, 0.1f, 0.1f);
  scene.boxes[0].material.kd = vec3(0.98f, 0.98f, 0.98f);
  scene.boxes[0].material.ks = vec3(0.98f, 0.98f, 0.98f);
  scene.boxes[0].material.ns = 128.0f;
  scene.boxes[0].material.reflt = 0.75f;
 
  return scene;
}

SCENE create_scene_2() {
  SCENE scene;
  scene.background = vec3(0.0f,0.0f,0.0f);

  scene.camera.position = vec3(-10.0f,15.0f,30.0f);
  scene.camera.view_up = vec3(0.0f,1.0f,0.0f);
  scene.camera.look_at = vec3(0.0f,0.0f,0.0f);
  scene.camera.fov = 35.0f;
 
  scene.nr_lights = 1;
  scene.lights[0].position = vec3(10.0f,25.0f,25.0f);
  scene.lights[0].la = vec3(1.0f,1.0f,1.0f);
  scene.lights[0].ld = vec3(1.0f,1.0f,1.0f);
  scene.lights[0].ls = vec3(1.0f,1.0f,1.0f);
  
  scene.nr_boxes = 1;
  scene.boxes[0].center = vec3(0.0f,0.0f,0.0f);
  scene.boxes[0].sx = 15.0f;
  scene.boxes[0].sy = 1.0f;
  scene.boxes[0].sz = 15.0f;
  scene.boxes[0].material.ka = vec3(0.2f, 0.2f, 0.2f);
  scene.boxes[0].material.kd = vec3(0.6f, 0.6f, 0.6f);
  scene.boxes[0].material.ks = vec3(0.98f, 0.98f, 0.98f);
  scene.boxes[0].material.ns = 64.0f;
  scene.boxes[0].material.reflt = 0.15f;
  
  scene.nr_spheres = 9;
  for (int i = 0; i < 3; i++) {
    for (int j = 0; j < 3; j++) {
      scene.spheres[i*3+j].center = vec3(-5.0f + j * 5.0f, 1.75f, -5.0f + i * 5.0f);
      scene.spheres[i*3+j].radius = 1.25f;

      if (i == 0) {
        scene.spheres[i*3+j].material.ka = vec3(0.1f, 0.1f, 0.1f);
        scene.spheres[i*3+j].material.kd = vec3(0.8f, 0.8f, 0.08f);
        scene.spheres[i*3+j].material.ks = vec3(0.98f, 0.98f, 0.98f);
        scene.spheres[i*3+j].material.ns = 64.0f;
        scene.spheres[i*3+j].material.reflt = 0.5f;
      } else if (i == 1) {
        scene.spheres[i*3+j].material.ka = vec3(0.1f, 0.1f, 0.1f);
        scene.spheres[i*3+j].material.kd = vec3(0.08f, 0.08f, 0.8f);
        scene.spheres[i*3+j].material.ks = vec3(0.98f, 0.98f, 0.98f);
        scene.spheres[i*3+j].material.ns = 64.0f;
        scene.spheres[i*3+j].material.reflt = 0.5f;
      } else {
        scene.spheres[i*3+j].material.ka = vec3(0.1f, 0.1f, 0.1f);
        scene.spheres[i*3+j].material.kd = vec3(0.8f, 0.08f, 0.8f);
        scene.spheres[i*3+j].material.ks = vec3(0.98f, 0.98f, 0.98f);
        scene.spheres[i*3+j].material.ns = 64.0f;
        scene.spheres[i*3+j].material.reflt = 0.5f;
      }
    }
  }
  return scene;
}

void main(void)
{
  SCENE scene = create_scene_1();
  fragColor = vec4(ray_tracing(scene, v_position), 1);
}
