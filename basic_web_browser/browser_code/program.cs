using System;
using System.Windows.Forms;
using System.Drawing;
using System.Collections.Generic;
using System.IO;

namespace WebBrowser
{
    /// <summary>
    /// Class <c>Program</c> for Starting Web Browser
    /// </summary>
    public class Program : Form
    {

        public String homeAddress;
        public List< string[] > historyList, favouritesList;
        public Tab currentTab;

        private int historyIndex;
        private TabControl control;
        private EventHandler[] buttonHandlers;
        private MenuStrip menu;

        public Program()
        {
            MinimumSize = new Size( 450, 200 );
            Text = "Industrial Programming: F20SC - Web Browser";
            KeyPreview = true;

            /*
             * Setup Menu Strip
             * 
             */
            menu = new MenuStrip();
            menu.Location = new Point( 0, 0 );

            var goTo = new ToolStripMenuItem();
            goTo.Text = "Go To..";

            var history = new ToolStripMenuItem();
            goTo.DropDownItems.Add( history );
            history.Text = "History";
            history.Click += new EventHandler( show_history );
            history.ShortcutKeys = Keys.F1;

            var favourites = new ToolStripMenuItem();
            goTo.DropDownItems.Add( favourites );
            favourites.Text = "Favourites";
            favourites.Click += new EventHandler( show_favourites );
            favourites.ShortcutKeys = Keys.F2;

            menu.Items.Add( goTo );

            /*
             * Setup Tab Control
             */
            control = new TabControl();
            control.Width = Width - 15;
            control.Height = Height - 65;
            control.Location = new Point( 0, 30 );
            control.Anchor = ( AnchorStyles.Top | AnchorStyles.Right | AnchorStyles.Left | AnchorStyles.Bottom );
            control.SelectedIndexChanged += new EventHandler( change_tab );

            buttonHandlers = new EventHandler[] { go_home, home_update, new_tab, close_tab, go_back, go_forward };
            currentTab = new Tab( ref buttonHandlers, ref control );
            ( ( WebControl ) currentTab.Controls.Find( "webControl", true ) [ 0 ] ).setCurrentTab( currentTab );
            Controls.AddRange( new Control [] { menu, control } );

            /*
             * Get Stored Data
             */
            deserialize();

            if ( historyList.Count > 0 ) {
                currentTab.set_back( true );
                historyIndex = historyList.Count;
            }
        }

        /// <summary>
        /// Get Data From Local XML Files If Exists
        /// </summary>
        private void deserialize()
        {
            var xs = new System.Xml.Serialization.XmlSerializer(typeof(List<string[]>));
            var xy = new System.Xml.Serialization.XmlSerializer(typeof(string));
            using (FileStream fs = new FileStream(AppDomain.CurrentDomain.BaseDirectory + @"\history.xml", FileMode.OpenOrCreate))
            {
                if (fs.Length != 0)
                {
                    historyList = xs.Deserialize(fs) as List<string[]>;
                }
                else
                {
                    historyList = new List<String[]>();
                }
            }

            using (FileStream fs = new FileStream(AppDomain.CurrentDomain.BaseDirectory + @"\favourites.xml", FileMode.OpenOrCreate))
            {
                if (fs.Length != 0)
                {
                    favouritesList = xs.Deserialize(fs) as List<string[]>;
                }
                else
                {
                    favouritesList = new List<String[]>();
                }
            }

            using (FileStream fs = new FileStream(AppDomain.CurrentDomain.BaseDirectory + @"\homeaddress.xml", FileMode.OpenOrCreate))
            {
                if (fs.Length != 0)
                {
                    homeAddress = xy.Deserialize(fs) as string;
                }
                else
                {
                    homeAddress = "";
                }
            }
        }

        /// <summary>
        /// Get History Index
        /// </summary>
        /// <returns>History Index</returns>
        public int getIndex()
        {
            return historyIndex;
        }

        /// <summary>
        /// Set History Index
        /// </summary>
        /// <param name="index"></param>
        public void setIndex( int index )
        {
            historyIndex = index;
        }

        /// <summary>
        /// Event Handler When Home Needs Updated
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void home_update( object sender, EventArgs e )
        {
            homeAddress = ( ( WebControl ) currentTab.Controls.Find( "webControl", true ) [ 0 ] ).getAddress();
            MessageBox.Show( "Home Page Updated" );
        }

        /// <summary>
        /// Event Handler When Home Button Called
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void go_home( object sender, EventArgs e )
        {
            var webControl = ( ( WebControl ) currentTab.Controls.Find( "webControl", false ) [ 0 ] );
            webControl.setAddress( homeAddress );
            webControl.goToAddress( homeAddress, false );
        }

        /// <summary>
        /// Event Handler When New Tab Button Called
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void new_tab( object sender, EventArgs e )
        {
            currentTab = new Tab( ref buttonHandlers, ref control );
            control.SelectedTab = currentTab;
            ( ( WebControl ) currentTab.Controls.Find( "webControl", true ) [ 0 ] ).setCurrentTab( currentTab );

            if (historyList.Count > 0)
            {
                currentTab.set_back(true);
                historyIndex = historyList.Count;
            }
        }

        /// <summary>
        /// Event Handler On Tab Closing To Remove From List Of Tab Pages And Update Current Tab If Currently Showing
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void close_tab( object sender, EventArgs e )
        {
            if ( control.TabPages.Count > 1 )
            {
                control.TabPages.Remove( currentTab );
                currentTab = ( ( Tab ) control.SelectedTab );
            } else
            {
                this.Close();
            }
        }

        /// <summary>
        /// Event Handler To Show Form Containing Saved Histories
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void show_history( object sender, EventArgs e )
        {
            PopupForm m = new PopupForm( historyList, true, this );
            m.Show();
        }

        /// <summary>
        /// Event Handler To Show Form Containing Saved Favourites
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void show_favourites( object sender, EventArgs e )
        {
            PopupForm m = new PopupForm( favouritesList, false, this );
            m.Show();
        }

        /// <summary>
        /// Event Handler To Change Reference To Tab Currently In View
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void change_tab( object sender, EventArgs e )
        {
            currentTab = ( (Tab) control.SelectedTab );
        }

        /// <summary>
        /// Event Handler For Forward Button
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void go_back( object sender, EventArgs e )
        {
            Tab selected = ( ( Tab ) control.SelectedTab );
            historyIndex--;
            ( ( WebControl ) currentTab.Controls.Find( "webControl", false ) [ 0 ] ).goToAddress( historyList[ historyIndex ][ 1 ], true );
            if ( historyIndex != historyList.Count - 1 ) {
                selected.set_forward(true);
            }
            if ( historyIndex == 0 )
            {
                selected.set_back( false );
            }
        }

        /// <summary>
        /// Event Handler For Back Button
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void go_forward( object sender, EventArgs e )
        {
            Tab selected = ( (Tab) control.SelectedTab );
            if ( historyIndex != historyList.Count - 1 )
            {
                historyIndex++;
                ( ( WebControl ) currentTab.Controls.Find( "webControl", false ) [ 0 ] ).goToAddress( historyList[ historyIndex ][ 1 ], true );
                if ( historyIndex == 0 )
                {
                   selected.set_back( false );
                }
            }
            if ( historyIndex == historyList.Count -1 )
            {
                selected.set_forward( false );
            }
        }

        /// <summary>
        /// Restricts Layout Of Text Boxes When Resizing As Well As Impede Responsivness
        /// </summary>
        protected override void OnResizeBegin( EventArgs e )
        {
            SuspendLayout();
            base.OnResizeBegin( e );
        }

        protected override void OnResizeEnd( EventArgs e )
        {
            ResumeLayout();
            base.OnResizeEnd( e );
        }

        /// <summary>
        /// On Close Save Variables Into Local XML Files
        /// </summary>
        protected override void OnFormClosing( FormClosingEventArgs e )
        {
            base.OnFormClosing( e );
            var xs = new System.Xml.Serialization.XmlSerializer( typeof( List<string[]> ) );
            var xy = new System.Xml.Serialization.XmlSerializer( typeof( string ) );

            using (FileStream fs = new FileStream( AppDomain.CurrentDomain.BaseDirectory + @"\history.xml", FileMode.OpenOrCreate ) )
            {
                fs.SetLength(0);
                xs.Serialize( fs, historyList );
            }

            using (FileStream fs = new FileStream( AppDomain.CurrentDomain.BaseDirectory + @"\favourites.xml", FileMode.OpenOrCreate ) )
            {
                fs.SetLength(0);
                xs.Serialize( fs, favouritesList );
            }

            using (FileStream fs = new FileStream( AppDomain.CurrentDomain.BaseDirectory + @"\homeaddress.xml", FileMode.OpenOrCreate ) )
            {
                fs.SetLength(0);
                xy.Serialize( fs, homeAddress );
            }
        }

        static void Main( string[] args )
        {
            Application.Run( new Program() );
        }

    }
}
