using System;
using System.Windows.Forms;
using System.Drawing;

namespace WebBrowser
{
    /// <summary>
    /// Class <c>Tab</c> for creating indivdiual tab pages and are directly added into tab control reference passed in constructor
    /// </summary>
    public class Tab : TabPage
    {
        private Button[] tabButtons = new Button[ 6 ];

        public Tab( ref EventHandler[] buttonHandlers, ref TabControl controller )
        {
            if ( buttonHandlers.Length != 6 )
                throw new ArgumentException( "Must Have Six Button Handlers" );

            Text = "New Tab";
            Width = controller.Width;
            Font = new Font( "calibri", 14 );

            setup_button( ref tabButtons[ 0 ], 30, new EventHandler ( buttonHandlers[ 0 ] ), new Point( 2, 5 ), "" );
            setup_button( ref tabButtons[ 1 ], 100, new EventHandler ( buttonHandlers[ 1 ] ), new Point(tabButtons[ 0 ].Location.X + tabButtons[ 0 ].Width + 10, 5 ), "Set As Home" );
            setup_button( ref tabButtons[ 2 ], 30, new EventHandler ( buttonHandlers[ 2 ] ), new Point(tabButtons[ 1 ].Location.X + tabButtons[ 1 ].Width + 30, 5 ), "+" );
            setup_button( ref tabButtons[ 3 ], 30, new EventHandler ( buttonHandlers[ 3 ] ), new Point(tabButtons[ 2 ].Location.X + tabButtons[ 2 ].Width + 10, 5 ), "-" );
            setup_button( ref tabButtons[ 4 ], 30, new EventHandler ( buttonHandlers[ 4 ] ), new Point(tabButtons[ 3 ].Location.X + tabButtons[ 3 ].Width + 30, 5 ), '\u2190'.ToString() );
            setup_button( ref tabButtons[ 5 ], 30, new EventHandler ( buttonHandlers[ 5 ] ), new Point(tabButtons[ 4 ].Location.X + tabButtons[ 4 ].Width + 10, 5 ), '\u2192'.ToString() );

            tabButtons[ 0 ].BackgroundImage = Properties.Resources.home;
            tabButtons[ 0 ].BackgroundImageLayout = ImageLayout.Stretch;
            tabButtons[ 4 ].Enabled = false;
            tabButtons[ 5 ].Enabled = false;

            Controls.AddRange( tabButtons );

            var webControl = new WebControl();
            webControl.Name = "webControl";
            webControl.Width = controller.Width - 10;
            webControl.Anchor = ( AnchorStyles.Top | AnchorStyles.Right | AnchorStyles.Left | AnchorStyles.Bottom );
            webControl.Location = new Point( 0, tabButtons[ 2 ].Location.Y + tabButtons[ 2 ].Height + 5 );
            Controls.Add( webControl );

            controller.TabPages.Add( this );
        }

        /// <summary>
        /// Set Button For Tab Page
        /// </summary>
        /// <param name="button"></param>
        /// <param name="width"></param>
        /// <param name="handler"></param>
        /// <param name="point"></param>
        /// <param name="text"></param>
        private void setup_button( ref Button button, int width, EventHandler handler, Point point, String text )
        {
            if ( handler == null || point == null || text == null )
                throw new ArgumentException( "Argument Cannot Be Null" );
            button = new Button();
            button.Height = 30;
            button.Width = width;
            button.Click += handler;
            button.Location = point;
            button.Text = text;
            button.Font = new Font( "calibri", 12 );
        }

        /// <summary>
        /// If at first webpage or no elements in history then this setter disables back button (or enables again)
        /// </summary>
        /// <param name="enabled"></param>
        public void set_back( Boolean enabled )
        {
            if ( tabButtons.Length < 4 )
                throw new ArgumentException( "Argument Cannot Be Null" );
            tabButtons[ 4 ].Enabled = enabled;
            if ( tabButtons[ 4 ].Enabled != enabled )
                throw new Exception( "Set Failed" );
        }

        /// <summary>
        /// If the current history pointer is at the end of the history list then disable forward (or enable if not)
        /// </summary>
        /// <param name="enabled"></param>
        public void set_forward( Boolean enabled )
        {
            if ( tabButtons.Length < 5 )
                throw new ArgumentException( "Argument Cannot Be Null" );
            tabButtons[ 5 ].Enabled = enabled;
            if ( tabButtons[ 5 ].Enabled != enabled )
                throw new Exception( "Set Failed" );
        }
    }
}
