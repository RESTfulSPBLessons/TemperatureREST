export interface NavData {
  name?: string;
  url?: string;
  icon?: string;
  badge?: any;
  title?: boolean;
  children?: any;
  variant?: string;
  attributes?: object;
  divider?: boolean;
  class?: string;
}

export const navItems: NavData[] = [
  {
    name: 'Dashboard',
    url: '/dashboard',
    icon: 'icon-speedometer',
    badge: {
      variant: 'info',
      text: 'MAIN'
    }
  },
  {
    title: true,
    name: 'Menu'
  },
  {
    name: 'Information',
    url: '/theme/colors',
    icon: 'icon-drop'
  }
];
