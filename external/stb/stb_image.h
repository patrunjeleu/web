// STB Image placeholder - would be full library in production
// For this demo, we don't need texture loading
#ifndef STB_IMAGE_H
#define STB_IMAGE_H

#ifdef __cplusplus
extern "C" {
#endif

unsigned char *stbi_load(char const *filename, int *x, int *y, int *comp, int req_comp);
void stbi_image_free(void *retval_from_stbi_load);

#ifdef __cplusplus
}
#endif

#endif
