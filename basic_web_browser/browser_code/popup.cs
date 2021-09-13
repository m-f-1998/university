using System;
using System.Windows.Forms;
using System.Drawing;
using System.Collections.Generic;

namespace WebBrowser
{
    /// <summary>
    /// Class <c>PopupForm</c> Creates Form For History and Favourite PopUp Windows
    /// </summary>
    class PopupForm : Form
    {

        private int height;
        private Label title, address;
        private Button addFavourite;
        private Program parent;
        private List< string[] > toDisplay;
        private Boolean history;

        public PopupForm( List< string[] > toDisplay, Boolean history, Program parent )
        {
            if ( toDisplay == null || parent == null )
                throw new ArgumentException( "Argument Cannot Be Null" );
            MinimumSize = new Size( 550, 150 );
            MaximumSize = new Size( 550, int.MaxValue );

            this.parent = parent;
            this.toDisplay = toDisplay;
            this.history = history;

            if ( history )
            {
                title = initalise_label( "Title", Width / 2 - 10, 30, new Point( 0, 0 ), ContentAlignment.MiddleCenter, new Font( "calibri", 12 ) );
                address = initalise_label( "Link (Double-Click To Visit)", Width / 2 - 8, 30, new Point( Width / 2 - 10, 0), ContentAlignment.MiddleCenter, new Font( "calibri", 12 ) );
                height = title.Height;

                Controls.AddRange( new Label[] { title, address } );

                foreach ( string[] s in toDisplay )
                {
                    TextBox titleData, titleAddress;

                    titleData = initalise_text_box( new Point( 0, height ), s[ 0 ], Width / 2 - 10, HorizontalAlignment.Center, new Font( "calibri", 12 ) );
                    titleData.ReadOnly = true;
                    titleData.Anchor = ( AnchorStyles.Top | AnchorStyles.Left );

                    titleAddress = initalise_text_box( new Point( Width / 2 - 10, height ), s[ 1 ], Width / 2 - 8, HorizontalAlignment.Center, new Font( "calibri", 12 ) );
                    height += titleData.Height;
                    titleAddress.ReadOnly = true;
                    titleAddress.Anchor = ( AnchorStyles.Top | AnchorStyles.Right );
                    titleAddress.DoubleClick += new EventHandler( address_click );

                    Controls.AddRange( new TextBox[] { titleData, titleAddress } );
                }
            }
            else
            {
                FormClosed += new FormClosedEventHandler(save_favourites);

                addFavourite = initalise_button( new Point( 0, 0 ), "Add New Favourite", Width - 8, 30, new EventHandler( add_favourite ), ContentAlignment.MiddleCenter );
                addFavourite.Font = new Font("calibri", 12);
                height = addFavourite.Height + 5;

                title = initalise_label( "*Title*\n(Double-Click To Visit)", Width / 3 - 10, 30, new Point( 0, height ), ContentAlignment.MiddleCenter, new Font("calibri", 12 ) );
                address = initalise_label( "Link", Width / 3 - 8, 30, new Point( Width / 3 - 10, height ), ContentAlignment.MiddleCenter, new Font( "calibri", 12 ) );
                height += title.Height + 5;

                Controls.AddRange( new Control[] { addFavourite, title, address } );

                foreach ( string[] s in toDisplay )
                {
                    add_favourite( s, 0 );
                }
            }
        }

        /// <summary>
        /// Event Handler For <c>Add Favourite</c> Button In Window
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void add_favourite( object sender, EventArgs e )
        {
            add_favourite( new String[] { "", "" } , 1 );
        }

        /// <summary>
        /// Add Favourite Row To Popup Window
        /// </summary>
        /// <param name="text"></param>
        /// <param name="tag"></param>
        private void add_favourite( string[] text, int tag )
        {
            if ( text == null )
                throw new ArgumentException( "Argument Cannot Be Null" );
            TextBox titleData, titleAddress;
            Button deleteFav;

            titleData = initalise_text_box( new Point( 0, height ), text[ 0 ], Width / 3 - 10, HorizontalAlignment.Center, new Font( "calibri", 12 ) );
            titleData.Name = "name";
            titleData.Tag = tag;
            titleData.DoubleClick += new EventHandler(address_click);

            titleAddress = initalise_text_box( new Point( Width / 3 - 10, height ), text[ 1 ], Width / 3 - 8, HorizontalAlignment.Center, new Font( "calibri", 12 ) );
            titleAddress.Name = "address";
            titleAddress.Tag = tag;

            deleteFav = initalise_button( new Point( ( ( Width / 3 ) * 2 ) - 20, height ), "Delete", Width / 3 + 2, titleAddress.Height, new EventHandler( delete_fav ), ContentAlignment.MiddleCenter);
            height += titleData.Height;

            Controls.AddRange( new Control[] { titleData, titleAddress, deleteFav } );
        }

        /// <summary>
        /// Initalises All Buttons In Pop Up Forms
        /// </summary>
        /// <param name="point"></param>
        /// <param name="textString"></param>
        /// <param name="width"></param>
        /// <param name="height"></param>
        /// <param name="handler"></param>
        /// <param name="align"></param>
        /// <returns>Initalised Button</returns>
        private Button initalise_button( Point point, String textString, int width, int height, EventHandler handler, ContentAlignment align )
        {
            if ( point == null || textString == null || handler == null )
                throw new ArgumentException( "Argument Cannot Be Null" );
            Button returnButton = new Button();
            returnButton.Location = point;
            returnButton.Text = textString;
            returnButton.Width = width;
            returnButton.Height = height;
            returnButton.Click += handler;
            returnButton.TextAlign = align;
            return returnButton;
        }

        /// <summary>
        /// Initalises All Labels In Pop Up Forms
        /// </summary>
        /// <param name="textString"></param>
        /// <param name="width"></param>
        /// <param name="height"></param>
        /// <param name="point"></param>
        /// <param name="align"></param>
        /// <param name="font"></param>
        /// <returns>Initalised Label</returns>
        private Label initalise_label( String textString, int width, int height, Point point, ContentAlignment align, Font font )
        {
            if ( textString == null || point == null || font == null )
                throw new ArgumentException( "Argument Cannot Be Null" );
            Label returnLabel = new Label();
            returnLabel.Text = textString;
            returnLabel.Width = width;
            returnLabel.Height = height;
            returnLabel.Location = point;
            returnLabel.TextAlign = align;
            returnLabel.Font = font;
            return returnLabel;
        }
        
        /// <summary>
        /// Initalises All Text Boxes In Pop Up Forms
        /// </summary>
        /// <param name="point"></param>
        /// <param name="textString"></param>
        /// <param name="width"></param>
        /// <param name="align"></param>
        /// <param name="font"></param>
        /// <returns>Initalised Text Box</returns>
        private TextBox initalise_text_box( Point point, String textString, int width, HorizontalAlignment align, Font font )
        {
            if ( point == null || textString == null || font == null )
                throw new ArgumentException( "Argument Cannot Be Null" );
            TextBox returnBox = new TextBox();
            returnBox.Location = point;
            returnBox.Text = textString;
            returnBox.Width = width;
            returnBox.TextAlign = align;
            returnBox.Font = font;
            return returnBox;
        }

        /// <summary>
        /// Event Handler For If Address Is Double Clicked In Popup Form
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void address_click( object sender, EventArgs e )
        {
            Close();
            int n = ( ( ( TextBox ) sender ).Location.Y - 30 ) / 27; // Get TextBox Based On Location
            if ( !history ) { n = n - 1; }
            int i = 0;
            foreach( string[] p in toDisplay )
            {
                if ( i == n )
                {
                    ( ( WebControl ) parent.currentTab.Controls.Find( "webControl", false ) [ 0 ] ).goToAddress( p[ 1 ], false );
                    if ( !history )
                    {
                        parent.currentTab.Text = p[ 0 ];
                    }
                    return;
                }
                i++;
            }
        }

        /// <summary>
        /// Event Handler For Button To Delete Row In Popup Form
        /// </summary>
        /// 
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void delete_fav( object sender, EventArgs e )
        {
            string[] a = new string[] { "", "" };
            int numControls = Controls.Count;
            restart:  foreach ( Control x in Controls )
            {
                if ( x is TextBox && x.Location.Y == ( ( Button ) sender ).Location.Y )
                {
                    Controls.Remove( x ); // Jumps One Controller So Need To Restart
                    if ( a[ 0 ] == "" )
                    {
                        a[ 0 ] = x.Text;
                    }
                    else
                    {
                        a[ 1 ] = x.Text;
                        for ( int i = 0; i < parent.favouritesList.Count; i++ )
                        {
                            if ( parent.favouritesList[ i ][ 0 ] == a[ 0 ] && parent.favouritesList[ i ][ 1 ] == a[ 1 ] )
                            {
                                parent.favouritesList.RemoveAt( i );
                                break;
                            }
                        }
                        a = new string[] { "", "" };
                    }
                    goto restart;
                }
            }
            Controls.Remove( ( ( Button ) sender ) );
            if ( numControls - 3 != Controls.Count )
                throw new Exception( "Favourite Could Not Be Removed" );
        }
        
        /// <summary>
        /// Event Handler For Saving Favourites Upon Popup Window Closing
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void save_favourites( object sender, FormClosedEventArgs e )
        {
            string name = "";
            Boolean textBoxExisted = false, nameFound = false;

            foreach ( Control x in Controls)
            {
                if ( x is TextBox )
                {
                    if ( x.Name == "name" )
                    {
                        foreach( string[] s in toDisplay )
                        {
                            if ( s[ 0 ] == x.Text ) // If Name Found Already In Favourites List
                            {
                                if ( ( ( int ) x.Tag ) == 0 ) // If Text Box Was AutoFilled On Load
                                {
                                    textBoxExisted = true;
                                }
                                nameFound = true;
                                break;
                            }
                        }
                        name = x.Text;
                    }
                    else if ( x.Name == "address" )
                    {
                        if ( textBoxExisted && nameFound ) // If Data Looking At Was Autofilled and Name Found Twice
                        {
                            foreach ( string[] s in toDisplay )
                            {
                                if ( name == s[ 0 ] )
                                {
                                    s[ 1 ] = x.Text;
                                }
                            }
                            textBoxExisted = nameFound = false;
                        }
                        else
                        {
                            if ( name != "" || ( nameFound && !textBoxExisted ) ) // Name Not Empety & Name Not Already In Favourites List But WasFound Twice
                            {
                                toDisplay.Add( new string[] { name, x.Text } );
                            }
                        }
                    }
                } 
            }
        }
    }
}
