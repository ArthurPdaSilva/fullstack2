import '@mdi/font/css/materialdesignicons.css'
import { createVuetify } from 'vuetify'
import * as components from 'vuetify/components'
import * as directives from 'vuetify/directives'
import 'vuetify/styles'

export default createVuetify({
  components,
  directives,
  theme: {
    defaultTheme: 'light',
    themes: {
      light: {
        dark: false,
        colors: {
          primary: '#1565C0',
          secondary: '#0D47A1',
          accent: '#82B1FF',
          error: '#D32F2F',
          info: '#1976D2',
          success: '#388E3C',
          warning: '#F57C00',
          background: '#F5F5F5',
          surface: '#FFFFFF',
        },
      },
    },
  },
  defaults: {
    VBtn: {
      variant: 'flat',
      rounded: 'lg',
    },
    VCard: {
      elevation: 2,
      rounded: 'lg',
    },
    VTextField: {
      variant: 'outlined',
      density: 'comfortable',
    },
    VTextarea: {
      variant: 'outlined',
      density: 'comfortable',
    },
    VDialog: {
      width: '480',
    },
    VAppBar: {
      elevation: 0,
    },
  },
  icons: {
    defaultSet: 'mdi',
  },
})
